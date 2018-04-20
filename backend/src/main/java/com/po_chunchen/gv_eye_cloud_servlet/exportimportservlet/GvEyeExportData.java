package com.po_chunchen.gv_eye_cloud_servlet.exportimportservlet;

import com.google.appengine.api.datastore.DatastoreTimeoutException;
import com.google.cloud.ServiceOptions;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreException;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageClass;
import com.google.cloud.storage.StorageOptions;

import com.po_chunchen.gv_eye_cloud_servlet.common.Constants;
import com.po_chunchen.gv_eye_cloud_servlet.common.GvErrorCode;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by po-chunchen on 2017/10/26.
 */
public class GvEyeExportData extends HttpServlet {
    private static final long serialVersionUID = 8126789192972477663L;

    static Logger Log = Logger.getLogger(GvEyeExportData.class.getName());

    private String uuid = Constants.DEFAULT_PARAMETER_UUID;

    private Storage storage = null;

    private Datastore datastore = null;

    private boolean isUpdateDatastore = true;

    private String kind = Constants.DATASTORAGE_KIND;

    private char[] charNumWords = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
            'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
            'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G',
            'H', 'I', 'J', 'K','L', 'M', 'N', 'O', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    private int randomLengthValue = 6;

    private String randomKey() {
        Random random = new Random();

        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < randomLengthValue; i++) {
            buffer.append(charNumWords[random.nextInt(62)]);
        }

        return buffer.toString();
    }

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


        String exportData = req.getParameter(Constants.PARAMETER_EXPORT_DATA);
        String deviceUuid = req.getParameter(Constants.PARAMETER_DEVICE_UUID);
        String exportDataVersion = req.getParameter(Constants.PARAMETER_EXPORT_DATA_VERSION);
        String deviceType = req.getParameter(Constants.DEVICE_TYPE);

        if(deviceUuid.isEmpty()) {
            deviceUuid = uuid;
        }

        responseJson.put(Constants.RESPONSE_MESSAGE_ID, GvErrorCode.PARAMETER_ERROR.getCode());
        responseJson.put(Constants.RESPONSE_MESSAGE, GvErrorCode.PARAMETER_ERROR.getDescription());

        if(!exportData.isEmpty()) {

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

            String importUUID = randomKey();

//        checkNum = "yg91NU";

            do {

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

                /**
                 * 產生一組亂數，但不能跟server的db重複
                 * */

                Key taskKey = datastore.newKeyFactory().setKind(kind).newKey(importUUID);
                Entity task = datastore.get(taskKey);
                if(task!=null) {
                    importUUID = randomKey();
                } else {
                    break;
                }

            }while (true);


            storage = StorageOptions.getDefaultInstance().getService();

            Bucket bucket = storage.get(Constants.STORAGE_BUCKET_NAME);

            if(bucket == null) {
                bucket = storage.create(BucketInfo.newBuilder(Constants.STORAGE_BUCKET_NAME)
                        .setStorageClass(StorageClass.REGIONAL)
                        .setLocation("asia-east1")
                        .build());
//            bucket = storage.create(BucketInfo.of("gveye_camera_lists_storage"));
            }

            InputStream content = new ByteArrayInputStream(exportData.getBytes("UTF-8"));
            BlobId blobId = bucket.create(importUUID, content, "text/plain").getBlobId();

            KeyFactory keyFactory = datastore.newKeyFactory().setKind(kind);
            Key key = keyFactory.newKey(importUUID);
            Entity entity = Entity.newBuilder(key)
                    .set(Constants.DEVICE_UUID, deviceUuid)
                    .set(Constants.BUCKET_NAME, blobId.getBucket().toString())
                    .set(Constants.BLOB_NAME, blobId.getName().toString())
                    .set(Constants.IMPOERT_COUNT, 0)
                    .set(Constants.EXPORT_IP_ADDRESS, userIp)
                    .set(Constants.EXPORT_TIME, timeStamp)
                    .set(Constants.EXPOERT_DATA_VERSION, exportDataVersion)
                    .set(Constants.DEVICE_TYPE, deviceType)
                    .build();

            try {
                datastore.put(entity);

                errorCode = GvErrorCode.HTTP_OK;

            } catch (DatastoreTimeoutException e) {
                isUpdateDatastore = false;
                errorCode = GvErrorCode.DATASTORE_TIMEOUT_EXCEPTION;
//            throw new ServletException("GvEyeExportData error", e);
            } catch (DatastoreException e) {
                isUpdateDatastore = false;
                errorCode = GvErrorCode.DATASTORE_EXCEPTION;
//            throw new ServletException("GvEyeExportData error", e);
            }

            if(GvErrorCode.HTTP_OK.equals(errorCode)) {
                if(isUpdateDatastore) {
                    responseJson.put(Constants.RESPONSE_MESSAGE_ID, GvErrorCode.UPDATE_WILL_SUCCESS.getCode());
                    responseJson.put(Constants.RESPONSE_AUTHENTICATION_CODE,importUUID);
                    responseJson.put(Constants.RESPONSE_MESSAGE, GvErrorCode.UPDATE_WILL_SUCCESS.getDescription());
                }else {
                    responseJson.put(Constants.RESPONSE_MESSAGE_ID, GvErrorCode.UPDATE_WILL_FAIL.getCode());
                    responseJson.put(Constants.RESPONSE_MESSAGE, GvErrorCode.UPDATE_WILL_FAIL.getDescription());
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
