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
        (~/(?i)datetime|timestamp/)         : "Timestamp",
        (~/(?i)date/)                       : "java.sql.Date",
        (~/(?i)time/)                       : "java.sql.Time",
        (~/(?i)time/)                       : "java.sql.Time",
        (~/(?i)/)                           : "String"
]

FILES.chooseDirectoryAndSave("Choose directory", "Choose where to store generated files") { dir ->
    SELECTION.filter { it instanceof DasTable }.each { generate(it, dir) }
}

def generate(table, dir) {
    def className = generateClassName(table.getName(), true)
    def fields = calcFields(table)
    packageName = getPackageName(dir);
    new File(dir, className + ".java")
            .withPrintWriter("UTF-8", { out -> generate(out, className, fields, table) })
}

// 获取包路径
def getPackageName(dir) {
    return dir.toString().replaceAll("\\\\", ".").replaceAll("/", ".").replaceAll("^.*src(\\.main\\.java\\.)?", "") + ";"
}

def generate(out, className, fields, table) {
    out.println "package $packageName"
    out.println ""
    out.println ""
    generateImportClassInfo(out);
    out.println ""
    generateClassAnnotation(out, table.getName());
    out.println "public class $className implements Serializable {"
    out.println ""
    out.println ""
    out.println genSerialID()
    fields.each() {
        out.println ""
        out.println "\t/**"
        out.println "\t * ${it.commoent}"
        out.println "\t */"
        generateFieldAnnotation(out, it);
        out.println "\tprivate ${it.type} ${it.name};"
    }
    out.println ""
    out.println "}"
}

def calcFields(table) {
    DasUtil.getColumns(table).reduce([]) { fields, col ->
        def spec = Case.LOWER.apply(col.getDataType().getSpecification())
        def typeStr = typeMapping.find { p, t -> p.matcher(spec).find() }.value
        fields += [[
                           name     : javaName(col.getName(), false),
                           type     : typeStr,
                           fieldName: col.getName(),
                           commoent : col.getComment(),
                           annos    : ""]]
    }
}


def generateClassName(str, capitalize) {
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

// 生成字段上的注解
def generateFieldAnnotation(out, field) {
    if (field.name == "id") {
        generateId(out);
    } else if (field.name == "createTime") {
        generateCreateTime(out);
    } else if (field.name == "updateTime") {
        generateUpdateTime(out);
    } else if (field.name == "deleted") {
        generateBoolean(out);
    } else {
        generateGeneral(out, field);
    }
}

// 生成通用的注解
def generateGeneral(out, field) {
    out.println "\t@Basic";
    out.println "\t@Column(name = \"" + field.fieldName + "\")";
}

// 生成组件ID
def generateId(out) {
    out.println "\t@Id";
    out.println "\t@Column(name = \"id\", nullable = false)";
    out.println "\t@GeneratedValue(strategy = GenerationType.IDENTITY)";
}

// 生成创建时间的注解
def generateCreateTime(out) {
    out.println "\t@Basic";
    out.println "\t@Column(name = \"create_time\", nullable = true)";
    out.println "\t@JsonFormat(pattern = \"yyyy-MM-dd HH:mm:ss\", timezone = \"GMT+8\")";
    out.println "\t@CreationTimestamp";
}

// 生成更新时间的注解
def generateUpdateTime(out) {
    out.println "\t@Basic";
    out.println "\t@Column(name = \"update_time\", nullable = true)";
    out.println "\t@JsonFormat(pattern = \"yyyy-MM-dd HH:mm:ss\", timezone = \"GMT+8\")";
    out.println "\t@UpdateTimestamp";
}

// 生成布尔类型
def generateBoolean(out) {
    out.println "\t@Basic";
    out.println "\t@Column(name = \"deleted\", columnDefinition = \"tinyint(2) DEFAULT 0 COMMENT '是否删除: 1 删除 0  存在'\")";
    out.println "\t@Type(type = \"org.hibernate.type.NumericBooleanType\")";
}


// 生成导入类信息
def generateImportClassInfo(out) {
    out.println "import com.fasterxml.jackson.annotation.JsonFormat;";
    out.println "import lombok.Data;";
    out.println "import org.hibernate.annotations.CreationTimestamp;";
    out.println "import org.hibernate.annotations.Type;";
    out.println "import org.hibernate.annotations.UpdateTimestamp;";
    out.println "import javax.persistence.*;";
    out.println "import java.io.Serializable;";
    out.println "import java.sql.Timestamp;";
}

// 生成类的注解
def generateClassAnnotation(out, tableName) {
    out.println "@Data";
    out.println "@Entity";
    out.println "@Table(name = \"" + tableName + "\", catalog = \"\")";
}

static String genSerialID() {
    return "\tprivate static final long serialVersionUID = " + Math.abs(new Random().nextLong()) + "L;";
}
