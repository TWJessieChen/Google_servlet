package com.po_chunchen.gv_eye_cloud_servlet.datastoreservlet;


import com.google.appengine.api.datastore.DatastoreService;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by po-chunchen on 2017/10/26.
 */
public class GvEyeUploadAppengineDatastoreServlet extends HttpServlet {
    private static final long serialVersionUID = 8126789192972477663L;

    static Logger Log = Logger.getLogger(GvEyeUploadAppengineDatastoreServlet.class.getName());

    private String check_num = "";

    private DatastoreService datastore;

    private String kind = "gv_eye_camera_list";


    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

//        String userIp = req.getRemoteAddr();
//
//        Log.log(Level.INFO, "Request input stream: " + req.getInputStream());
//
//        InetAddress address = InetAddress.getByName(userIp);
//        if (address instanceof Inet6Address) {
//            // nest indexOf calls to find the second occurrence of a character in a string
//            // an alternative is to use Apache Commons Lang: StringUtils.ordinalIndexOf()
//            userIp = userIp.substring(0, userIp.indexOf(":", userIp.indexOf(":") + 1)) + ":*:*:*:*:*:*";
//        } else if (address instanceof Inet4Address) {
//            userIp = userIp.substring(0, userIp.indexOf(".", userIp.indexOf(".") + 1)) + ".*.*";
//        }
//
//        Log.log(Level.INFO, "Request gv-eye upload database ip: " + userIp);


//        StringBuffer jb = new StringBuffer();
//        String line = "";
//        JSONObject jsonObject = null;
//        try {
//            BufferedReader reader = new BufferedReader(new InputStreamReader((ServletInputStream) req.getInputStream(), "utf-8"));
////            BufferedReader reader = req.getReader();
//            while ((line = reader.readLine()) != null)
//                jb.append(line);
//        } catch (Exception e) { /*report an error*/ }
//
//        try {
//            jsonObject= new JSONObject(jb.toString());
////            jsonObject =  HTTP.toJSONObject(jb.toString());
//        } catch (JSONException e) {
//            // crash and burn
//            throw new IOException("Error parsing JSON, request content: " + line);
//        }
//
//        String dbContent = jsonObject.getString("db_content");
//
//        String encryptionValue = jsonObject.getString("encryption_value");

//        if(dbContent.isEmpty()) {
//            //沒有拿到db content.
//            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//        }

//        String url;
//        //抓取當下系統時間，來當作doc節點!!!
//        String timeStamp = Long.toString(System.currentTimeMillis());
//
//
//        datastore = DatastoreServiceFactory.getDatastoreService();
//
//        check_num = UUID.randomUUID().toString();
//
//        Entity task = new Entity(kind);
//        task.setProperty("check_num", check_num);
//        task.setProperty("db_content", "Jessie test insert data" + timeStamp);
//        task.setProperty("download_count", 0);
//        task.setProperty("upload_ip_address", userIp);
//        task.setProperty("upload_time", timeStamp);
//        datastore.put(task);

//        Query q = new Query(kind).;
//        PreparedQuery pq = datastore.prepare(q);
//        FetchOptions fetchOptions = FetchOptions.Builder.withDefaults();
//        QueryResultList<Entity> results;
//        try {
//            results = pq.asQueryResultList(fetchOptions);
//        } catch (IllegalArgumentException e) {
//            // IllegalArgumentException happens when an invalid cursor is used.
//            // A user could have manually entered a bad cursor in the URL or there
//            // may have been an internal implementation detail change in App Engine.
//            // Redirect to the page without the cursor parameter to show something
//            // rather than an error.
//            return;
//        }

//        Query<Entity> query = Query.newEntityQueryBuilder()
//                .setKind(kind)
//                .build();

        /**
         * 產生一組亂數，但不能跟server的db重複
         * */
//        boolean isReRandom = false;
//        check_num = UUID.randomUUID().toString();




//            do {
//
//                try (ResultSet rs = conn.prepareStatement(selectCheckNumSql).executeQuery()) {
//
//                    if(!rs.next()) {
//                        isReRandom = false;
//                    }else{
//                        isReRandom = true;
//                        check_num = UUID.randomUUID().toString();
//                        selectCheckNumSql = "SELECT * FROM CLIENT_DB WHERE check_num='" + check_num + "'";
//
//                    }
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                    throw new ServletException();
//                }
//
//            }while (isReRandom);
//            resp.getWriter().println("444");
//            String insertSql = "insert into CLIENT_DB (check_num,encryption_value,db_content,upload_time,upload_ip_address,download_count,delete_flag) "
//                    + "values('" + check_num + "','" +
//                    encryptionValue + "','" +
//                    dbContent + "','" +
//                    timeStamp + "','" +
//                    userIp + "','" +
//                    "0"  + "','" +
//                    "0" + "')";
//
//
//            PreparedStatement stmt = conn.prepareStatement(insertSql);
//            stmt.executeUpdate();
//
//            Log.log(Level.INFO, "Insert db will success: " + insertSql);


//            String outString;
//            outString = "Response: ";
//            for (Entity entity : results) {
//                outString.concat("" + entity.getProperty("check_num"));
//            }
//            resp.getWriter().println(outString);
        resp.getWriter().println("Not Working.");
//            resp.getWriter().println("Insert db will success: " + insertSql);


    }


}
