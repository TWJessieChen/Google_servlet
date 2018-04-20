package com.po_chunchen.gv_eye_cloud_servlet.exportimportservlet;

import com.google.appengine.api.datastore.DatastoreTimeoutException;
import com.google.cloud.ServiceOptions;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreException;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import com.po_chunchen.gv_eye_cloud_servlet.common.Constants;
import com.po_chunchen.gv_eye_cloud_servlet.common.GvErrorCode;

import org.json.JSONObject;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by po-chunchen on 2017/10/26.
 */
public class GvEyeImportData extends HttpServlet {
    private static final long serialVersionUID = 8126789192972477663L;

    static Logger Log = Logger.getLogger(GvEyeImportData.class.getName());

    private Datastore datastore = null;

    private Storage storage = null;

    private String kind = Constants.DATASTORAGE_KIND;

    private static final int BUFFER_SIZE = 64 * 1024;

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        /**
         * start init response code.
         * */
        GvErrorCode errorCode;
        JSONObject responseJson = new JSONObject();
        /**
         * end init response code.
         * */


        String auth_code = req.getParameter("authentication_code");
//        String androidUuid = req.getParameter("android_uuid");

        responseJson.put(Constants.RESPONSE_MESSAGE_ID, GvErrorCode.PARAMETER_ERROR.getCode());
        responseJson.put(Constants.RESPONSE_MESSAGE, GvErrorCode.PARAMETER_ERROR.getDescription());

        if(!auth_code.isEmpty()) {

            String userIp = req.getRemoteAddr();

            Log.log(Level.INFO, "Request input stream: " + req.getInputStream());

            InetAddress address = InetAddress.getByName(userIp);
            if (address instanceof Inet6Address) {
                // nest indexOf calls to find the second occurrence of a character in a string
                // an alternative is to use Apache Commons Lang: StringUtils.ordinalIndexOf()
                userIp = userIp.substring(0, userIp.indexOf(":", userIp.indexOf(":") + 1)) + ":*:*:*:*:*:*";
            } else if (address instanceof Inet4Address) {
                userIp = userIp.substring(0, userIp.indexOf(".", userIp.indexOf(".") + 1)) + ".*.*";
            }

            Log.log(Level.INFO, "Request gv-eye upload database ip: " + userIp);

            String url;
            //抓取當下系統時間，來當作doc節點!!!
            String timeStamp = Long.toString(System.currentTimeMillis());

            String projectId = ServiceOptions.getDefaultProjectId();

//        datastore = DatastoreOptions.newBuilder().setProjectId("gveyecloudserver").build().getService();
//
//        ServletContext context = getServletContext();
//        InputStream in = context.getResourceAsStream("/WEB-INF/application_default_credentials.json");

            datastore = DatastoreOptions.newBuilder()
                    .build().getService();

//        checkNum = "yg91NU";


//            Query<ProjectionEntity> query = Query.newProjectionEntityQueryBuilder()
//                    .setKind(kind)
//                    .setFilter(StructuredQuery.PropertyFilter.eq("check_num", checkNum))
//                    .build();

//            Query<ProjectionEntity> query = Query.newProjectionEntityQueryBuilder()
//                    .setKind(kind)
//                    .setFilter(StructuredQuery.PropertyFilter.eq("ID", checkNum))
//                    .build();
//
//            QueryResults<ProjectionEntity> tasks = datastore.run(query);

//            Query<Entity> query = Query.newEntityQueryBuilder()
//                    .setKind(kind)
//                    .setFilter(StructuredQuery.PropertyFilter.eq("ID", checkNum))
//                    .build();
//
//            QueryResults<Entity> tasks = datastore.run(query);
//
//            if(!tasks.hasNext()) {
//                isReRandom = true;
//            } else {
//                checkNum = randomKey();
//            }

            boolean isGetCameraLists = false;

            String blobName = "";
            String bucketName = "";
            String exportDataVersion = "";
            try {
                Key taskKey = datastore.newKeyFactory().setKind(kind).newKey(auth_code);
                Entity task = datastore.get(taskKey);
                if(task!=null) {
                    bucketName = task.getString(Constants.BUCKET_NAME);
                    blobName = task.getString(Constants.BLOB_NAME);
                    exportDataVersion = task.getString(Constants.EXPOERT_DATA_VERSION);
                }

                if(blobName.isEmpty()) {
                    errorCode = GvErrorCode.PARAMETER_ERROR;
                } else {
                    errorCode = GvErrorCode.HTTP_OK;
                }


            } catch (DatastoreTimeoutException e) {
//            throw new ServletException("GvEyeImportData error", e);
                errorCode = GvErrorCode.DATASTORE_TIMEOUT_EXCEPTION;
            } catch (DatastoreException e) {
//            throw new ServletException("GvEyeImportData error", e);
                errorCode = GvErrorCode.DATASTORE_EXCEPTION;
            }

            if(GvErrorCode.HTTP_OK.equals(errorCode)) {
                String query_db_content = "";

                storage = StorageOptions.getDefaultInstance().getService();

                Bucket bucket = storage.get(Constants.STORAGE_BUCKET_NAME);

                if(bucket != null) {
                    isGetCameraLists = true;
                    Blob blob = bucket.get(blobName);
                    if (blob == null) {
                        isGetCameraLists = false;
                    } else {
                        //blob.getSize() < 1_000_000) {
                        byte[] content = blob.getContent();
                        query_db_content = new String(content);
                    }

                } else {
                    isGetCameraLists = false;
                }

                if(isGetCameraLists) {
                    responseJson.put(Constants.RESPONSE_MESSAGE_ID, GvErrorCode.DOWNLOAD_CAMERA_LISTS_WILL_SUCCESS.getCode());
                    responseJson.put(Constants.RESPONSE_IMPORT_DATA, query_db_content);
                    responseJson.put(Constants.RESPONSE_MESSAGE, GvErrorCode.DOWNLOAD_CAMERA_LISTS_WILL_SUCCESS.getDescription());
                    responseJson.put(Constants.RESPONSE_EXPORT_DATA_VERSION, exportDataVersion);
                }else {
                    responseJson.put(Constants.RESPONSE_MESSAGE_ID, GvErrorCode.DOWNLOAD_CAMERA_LISTS_WILL_FAIL.getCode());
                    responseJson.put(Constants.RESPONSE_MESSAGE, GvErrorCode.DOWNLOAD_CAMERA_LISTS_WILL_FAIL.getDescription());
                }

            } else if(GvErrorCode.DATASTORE_TIMEOUT_EXCEPTION.equals(errorCode)) {
                responseJson.put(Constants.RESPONSE_MESSAGE_ID, GvErrorCode.DATASTORE_TIMEOUT_EXCEPTION.getCode());
                responseJson.put(Constants.RESPONSE_MESSAGE, GvErrorCode.DATASTORE_TIMEOUT_EXCEPTION.getDescription());
            } else if(GvErrorCode.DATASTORE_EXCEPTION.equals(errorCode)) {
                responseJson.put(Constants.RESPONSE_MESSAGE_ID, GvErrorCode.DATASTORE_EXCEPTION.getCode());
                responseJson.put(Constants.RESPONSE_MESSAGE, GvErrorCode.DATASTORE_EXCEPTION.getDescription());
            }
        }

        resp.getWriter().println(responseJson.toString());

    }


}
