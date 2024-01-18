package slack.java;
 

 // SERVER
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
 
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
// .env
import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;

 
public class App {
 
    public static void main(String[] args) throws LifecycleException {
        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir("temp");
        tomcat.setPort(8080);
         
        String contextPath = "/";
        String docBase = new File(".").getAbsolutePath();
         
        Context context = tomcat.addContext(contextPath, docBase);
         
        HttpServlet servlet = new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp)
                    throws ServletException, IOException {
                
                // Extract data from the URL
                String pathInfo = req.getPathInfo();
                String[] pathParts = pathInfo.split("/");
                String data = (pathParts.length > 1) ? pathParts[1] : null;

                // Pass the data to the Ravelin class
                Ravelin ravelinInstance = new Ravelin();
                if (data != null) {
                    String  custid =  String.valueOf(pathParts[1]);
                    try{
                        String ravelinfetcheddata = ravelinInstance.ravelinReq(custid);

                        PrintWriter writer = resp.getWriter();
                    
                        writer.println("hello world");
                        writer.println(writer);
                        writer.println(ravelinfetcheddata);
                    }catch(InterruptedException e){

                    }
                    
                }
            }
        };
         
        String servletName = "Servlet1";
        String urlPattern = "/ravelin-data/*";
         
        tomcat.addServlet(contextPath, servletName, servlet);      
        context.addServletMappingDecoded(urlPattern, servletName);
         
        tomcat.start();
        tomcat.getServer().await();
        
    }
}

class Ravelin {

    public static String ravelinReq(String reqbody) throws IOException, InterruptedException {
        Dotenv dotenv = null;
        dotenv = Dotenv.configure().load();
        String ravelinkey = dotenv.get("RAVELIN_API_KEY");
        String userid = reqbody;
    
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("https://api.ravelin.com/v2/connect/customers/%s?depth=0&features=true", userid)))
                .header("Authorization", ravelinkey)
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        return response.body();
    }
}



// .env has to be in the same directry as the build.gradle.kts file