package st3s0f.scaffoldermavenplugin;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;
import com.github.javaparser.utils.SourceRoot;
import io.vavr.Tuple2;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class JavaParserUtils {

    private JavaParserUtils() {
    }

    public static Boolean hasAnyAnnotation(Path rootPath, final String annotaion) {
        try {
            return new SourceRoot(rootPath)
                    .tryToParse()
                    .stream()
                    .filter(pr -> pr.isSuccessful())
                    .map(pr -> pr.getResult().get())
                    .map(cu -> cu.accept(new GenericVisitorAdapter<Optional<com.github.javaparser.ast.Node>, Void>() {
                        @Override
                        public Optional<com.github.javaparser.ast.Node> visit(NormalAnnotationExpr n, Void arg) {
                            super.visit(n, arg);

                            return annotaion.equalsIgnoreCase(n.getNameAsString())
                                    ? n.getParentNode() : Optional.empty();
                        }
                    }, null))
                    .flatMap(o -> o.stream())
                    .findAny()
                    .map(n -> Boolean.TRUE)
                    .orElse(Boolean.FALSE)
                    ;

        } catch (IOException e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }
    }

    public static void save(Path pkgPath, String filename, String content) throws MojoExecutionException {
        try {
            FileUtils.write(
                    Paths.get(
                            pkgPath.toAbsolutePath().toString(),
                            filename + ".java"
                    ).toFile(),
                    content
            );
        } catch (IOException e) {
            throw new MojoExecutionException(e.toString());
        }
    }

    public static void createSpringBootApp(Path rootJavaSourcePath) throws MojoExecutionException {
        JPCreateClassInfo ccInfo = JavaParserUtils.createClass(rootJavaSourcePath, "App");

        ccInfo.getCoid().addAnnotation(SpringBootApplication.class);
        ccInfo.getCoid()
                .addMethod("main", Modifier.Keyword.PUBLIC, Modifier.Keyword.STATIC)
                .addParameter("String[]", "args")
                .setBody(new BlockStmt().addStatement("SpringApplication.run(Application.class, args);"))
        ;

        save(ccInfo.getClassPkgPath(), ccInfo.getCoid().getNameAsString(), ccInfo.getCu().toString());
    }

    private static JPCreateClassInfo createClass(Path rootJavaSourcePath, String fqName) {
        CompilationUnit cu = new CompilationUnit();
        Tuple2<String, String> pN = Utils.getPackageAndClassPartsFor(fqName);

        ClassOrInterfaceDeclaration coid = cu
                .setPackageDeclaration(pN._1())
                .addClass(pN._2());

        Path pkgPath = Path.of(
                rootJavaSourcePath.toString(),
                pN._1().replaceAll("\\.", "/")
        );

        return new JPCreateClassInfo(cu, coid, pkgPath);
    }

    public static void createJpaEntity(Path rootJavaSourcePath, String fqName) throws MojoExecutionException {
        JPCreateClassInfo ccInfo = createClass(rootJavaSourcePath, fqName);

        ccInfo.getCoid().addAnnotation(lombok.Data.class);

        JavaParserUtils.save(ccInfo.getClassPkgPath(), ccInfo.getCoid().getNameAsString(), ccInfo.getCu().toString());
    }
}
