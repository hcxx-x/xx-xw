import groovy.json.JsonSlurper

static void main(String[] args) {


  try{
    List<String>params = new ArrayList<String>()
    URL apiUrl = new URL('http://gitlab.cmcc.baiyaodajiankang.cn/api/v4/projects/253/repository/branches')
    def connection = (HttpURLConnection) apiUrl.openConnection()
    connection.setRequestMethod("GET")
    connection.setRequestProperty("PRIVATE-TOKEN", "jomzPyGSFTBPQmasnyWF")
    connection.setDoOutput(true)
    connection.connect()
    List json = new JsonSlurper().parse(connection.getInputStream())
    println(json)
    for (repo in json ) {
      params.add(repo.name)
    }
    println params
  }
  catch(IOException ex){
    print ex
  }

}