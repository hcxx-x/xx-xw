import com.intellij.database.model.DasTable
import com.intellij.database.util.Case
import com.intellij.database.util.DasUtil

// 包文件路径 D:\install\IntelliJ IDEA 2019.3.3\plugins\DatabaseTools\lib
// database-openapi.jar\com\intellij\database\model
/*
 * Available context bindings:
 *   SELECTION   Iterable<DasObject>
 *   PROJECT     project
 *   FILES       files helper
 */

packageName = "com.sample;"
/* 参数类型映射 */
typeMapping = [
        (~/(?i)int/)                        : "Integer",
        (~/(?i)float|double|decimal|real/)  : "double",
        (~/(?i)tinyint|double|decimal|real/): "Boolean",
        (~/(?i)datetime|timestamp|date/)    : "Date",
        (~/(?i)time/)                       : "java.sql.Time",
        (~/(?i)/)                           : "String"
]

FILES.chooseDirectoryAndSave("Choose directory", "Choose where to store generated files") { dir ->
    SELECTION.filter { it instanceof DasTable }.each { generate(it, dir) }
}

def generate(table, dir) {
    def className = generateClassName(table.getName(), true)
    def entityName = generateEntityName(table.getName(), true)
    packageName = getPackageName(dir);
    new File(dir, className + ".java")
            .withPrintWriter("UTF-8", { out -> generate(out, className, entityName) })
}

// 获取包路径
def getPackageName(dir) {
    return dir.toString().replaceAll("\\\\", ".").replaceAll("/", ".").replaceAll("^.*src(\\.main\\.java\\.)?", "") + ";"
}

def generate(out, className, entityName) {
    out.println "package $packageName"
    out.println ""
    out.println ""
    out.println ""
    out.println "public interface $className extends JpaRepository<$entityName, Integer>  {"
    out.println ""
    out.println "}"
}

def generateClassName(str, capitalize) {
    def s = com.intellij.psi.codeStyle.NameUtil.splitNameIntoWords(str)
            .collect { Case.LOWER.apply(it).capitalize() }
            .join("")
            .replaceAll(/[^\p{javaJavaIdentifierPart}[_]]/, "_")
            .replace("Ant", "");

    s = s + "Repository";

    capitalize || s.length() == 1 ? s : Case.LOWER.apply(s[0]) + s[1..-1]
}

def generateEntityName(str, capitalize) {
    def s = com.intellij.psi.codeStyle.NameUtil.splitNameIntoWords(str)
            .collect { Case.LOWER.apply(it).capitalize() }
            .join("")
            .replaceAll(/[^\p{javaJavaIdentifierPart}[_]]/, "_")
            .replace("Ant", "");

    s = s + "Entity";

    capitalize || s.length() == 1 ? s : Case.LOWER.apply(s[0]) + s[1..-1]
}

def javaName(str, capitalize) {
    def s = com.intellij.psi.codeStyle.NameUtil.splitNameIntoWords(str)
            .collect { Case.LOWER.apply(it).capitalize() }
            .join("")
            .replaceAll(/[^\p{javaJavaIdentifierPart}[_]]/, "_")

    capitalize || s.length() == 1 ? s : Case.LOWER.apply(s[0]) + s[1..-1]
}