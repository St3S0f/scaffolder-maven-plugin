package st3s0f.scaffoldermavenplugin;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import lombok.Value;

import java.nio.file.Path;

@Value
public class JPCreateClassInfo {
    CompilationUnit cu;
    ClassOrInterfaceDeclaration coid;
    Path classPkgPath;
}
