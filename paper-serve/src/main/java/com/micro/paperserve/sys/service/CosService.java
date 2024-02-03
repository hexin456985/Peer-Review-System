package com.micro.paperserve.sys.service;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.GeneratePresignedUrlRequest;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.qcloud.cos.transfer.Download;
import com.qcloud.cos.transfer.TransferManager;
import org.springframework.web.multipart.MultipartFile;




public class CosService {
    COSCredentials cred = new BasicCOSCredentials("AKIDVt6A0lRyMchbFetASlrqAMKArRrAJJ6g", "RfdXxJVfkx2we9OMFgzDI9mGbpqiQ8ve");
    Region region = new Region("ap-nanjing");
    ClientConfig clientConfig = new ClientConfig(region);


    public URL generatePresignedUrl(String filename) {
        COSClient cosClient = new COSClient(cred, clientConfig);
        String bucketName = "devops-1301303353";
        // 设置过期时间
        Date expiration = new Date(System.currentTimeMillis() + 24*3600 * 1000);
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, filename)
                .withExpiration(expiration);

        return cosClient.generatePresignedUrl(request);
    }


    public String upload(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        // 实例化 COSClient
        COSClient cosClient = new COSClient(cred, clientConfig);
        String bucketName = "devops-1301303353"; // 替换为你的存储桶名称


        try (InputStream inputStream = file.getInputStream()) {
            // 创建上传请求对象
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, inputStream, null);

            // 执行上传
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);

            // 保存在 COS 的文件路径
            String cosFilePath = "https://" + bucketName + ".cos." + region + ".myqcloud.com/" + fileName;
            
            
            return cosFilePath;
        } catch (IOException e) {
            e.printStackTrace();
            return "Fail";
        }
        finally {
                // 关闭客户端
                if (cosClient != null) {
                    cosClient.shutdown();
                }
            }
    }

    public String download(String fileName, String localFilePath){
        COSClient cosClient = new COSClient(cred, clientConfig);
        ExecutorService threadPool = Executors.newFixedThreadPool(8);
        TransferManager transferManager = new TransferManager(cosClient, threadPool);

        String bucketName = "devops-1301303353";
        File downloadFile = new File(localFilePath);

        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, fileName);
        try{
            Download download = transferManager.download(getObjectRequest, downloadFile);
            download.waitForCompletion();
            return localFilePath;
        } catch (CosServiceException e) {
            e.printStackTrace();
        } catch (CosClientException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        transferManager.shutdownNow(true);
        return "Fail";
    }
}
