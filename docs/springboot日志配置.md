一、在springboot的yml 配置文件中对日志输出进行简单配置

springboot 默认使用的日志框架是logback，在yml配置文件中也可以对logback进行简单的配置，可配置内容如下：

```yaml
logging:
  # 日志级别，可以为每个包配置不同的日志输出级别，当代码中的日志输出级别大于这个配置时，对应的日志才会被输出（eg: com.xx包的日志打印级别为waring,则该包下所有代码中的debug和info级别的日志不会被打印，而error和fatal级别的日志则会被打印）
  level:
    root: warn
    com.xx: info
  file:
    # logging.file.path好像没有什么用，至少在我这次测试中是没有起到作用的，无论配置是D:/weblogs 还是 ../weblogs (不生效的原因：path和file二者不能同时使用，如若同时使用，则只有logging.file生效)
    # path: D:/weblogs
    # logging.file.name 可以是一个文件名称，也可以是一个路径，路径以“/"开头则表示根路径，路径不以“/"开头则表示相对路径（在windows环境中，非jar包运行时，会从最上一层的父级目录开始生成对应的文件夹以及文件）
    name: wxlogs/web.log
  logback:
    rollingpolicy:
      clean-history-on-start: false
      # 滚动日志历史文件命名规则 默认：${LOG_FILE}.%d{yyyy-MM-dd}.%i.gz，注意：日期中不能出现“:” (如果配置使用双引号包裹起来应该可以，下面的配置也有类似问题) 否则会使配置失效，如果出现时分秒，可以使用其他符号连接，而不要使用“:”
      file-name-pattern: wxlogs/web.%d{yyyy-MM-dd-HH-mm-ss}.%i.log
      # 日志保留最大期限，该配置和 file-name-pattern配置有关，如果file-name-pattern精确到天，则max-history则为日志保留的最大天数，如果file-name-pattern精确到秒，则max-history则为日志保留的最大秒数
      max-history: 7
      max-file-size: 10KB
      total-size-cap: 1GB

  # group 可以对代码以包的形式或者类的形式进行分组，如下 xxxw: com.xx 表示的含义就是为com.xx这个包单独封一个组，xxxw就是这个组名，分完组之后对于logging.level的配置就可以不指定包名，直接指定组名就可以了 如：logging.level.xxxw: debug
  # springboot 预定义了一些组 可以在官方文档中查询  地址：https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.logging
  group:
    xxxw: com.xx
  pattern:
    #在控制台输入日志格式，默认值："%clr(%d{${logging.pattern.dateformat:-yyyy-MM-dd HH:mm:ss.SSS}}){faint} %clr(${logging.pattern.level:-%5p}) %clr(${PID:- }){magenta} %clr(---){faint} %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n${logging.exception-conversion-word:-%wEx}"
    console: "%clr(%d{${logging.pattern.dateformat:-yyyy-MM-dd HH:mm:ss.SSS}}){faint} %clr(${logging.pattern.level:-%5p}) %clr(${PID:- }){magenta} %clr(---){faint} %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n${logging.exception-conversion-word:-%wEx}"
    # 在日志文件中输出的日志格式，默认值  "%d{${logging.pattern.dateformat:-yyyy-MM-dd HH:mm:ss.SSS}} ${logging.pattern.level:-%5p} ${PID:- } --- [%t] %-40.40logger{39} : %m%n${logging.exception-conversion-word:-%wEx}"
    file: "%d{${logging.pattern.dateformat:-yyyy-MM-dd HH:mm:ss.SSS}} ${logging.pattern.level:-%5p} ${PID:- } --- [%t] %-40.40logger{39} : %m%n${logging.exception-conversion-word:-%wEx}"
    # 配置时间格式化样式 默认值 "yyyy-MM-dd HH:mm:ss.SSS"
    dateformat: "yyyy-MM-dd HH:mm:ss.SSS"
    # 渲染日志级别时使用的格式 默认 “%5p”
    level: "%5p"
  # 定义当日志发生异常时的转换字 (没明白啥意思)
  exception-conversion-word: "%wEx"
  charset:
    # 配置控制台日志输出的字符集编码
    console: UTF-8
    # 配置日志文件输出的字符集编码
    file: UTF-8
  # 在yml文件中只能对日志进行简单的配置.如果需要更加细致的配置则需要使用特定的日志配置文件，logging.config 指定配置文件的名称 (spring官方推荐使用带-spring后缀的文件)，下面的配置值没有经过验证！
  # 这项配置可以不配置，springboot 会自动去查找classpath下的 logback配置（logback.xml,logback-spring.xml,logback-spring.groovy、 logback.groovy); log4j2配置 (log4j2-spring.xml、log4j2.xml); Java Util Logging 配置（logging.properties）
  config: classpath:logback-spring.xml


```

