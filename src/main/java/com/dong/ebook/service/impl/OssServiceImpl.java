package com.dong.ebook.service.impl;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.dong.ebook.dto.ResponseUploadDto;
import com.dong.ebook.model.User;
import com.dong.ebook.service.OssService;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class OssServiceImpl implements OssService {
    String accessId = "LTAIRjwzdCuZyylM"; // 请填写您的AccessKeyId。
    String accessKey = "KI6Ytx3cJ96q0Bx3ENC3aTlV1xeiyY"; // 请填写您的AccessKeySecret。
    String endpoint = "oss-cn-beijing.aliyuncs.com"; // 请填写您的 endpoint。
    String bucket = "e-bookpublic"; // 请填写您的 bucketname 。
    String host = "http://" + bucket + "." + endpoint; // host的格式为 bucketname.endpoint
    String avatarDir = "avatar/"; // 用户上传文件时指定的前缀 如：user-avatarDir-prefix/
    String blogImgDir = "blogImg/";
    String bookDir = "book/";
    String videoDir = "video/";
    String musicDir = "music/";
    String pictureDir = "picture/";

    public ResponseUploadDto upload(String dir, String filename) throws UnsupportedEncodingException {
        ResponseUploadDto responseUploadDto = new ResponseUploadDto();

        OSSClient client = new OSSClient(endpoint, accessId, accessKey);
        //设置policy过期时间
        long expireTime = 30;
        long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
        Date expiration = new Date(expireEndTime);
        PolicyConditions policyConds = new PolicyConditions();
        //设置上传文件的大小限制100M
        policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 104857600);
        //设置前缀
        policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

        String postPolicy = client.generatePostPolicy(expiration, policyConds);
        byte[] binaryData = postPolicy.getBytes("utf-8");
        String encodedPolicy = BinaryUtil.toBase64String(binaryData);
        String postSignature = client.calculatePostSignature(postPolicy);

        //filename包含了路径
        filename = dir + filename;
        String fileUrl = host + filename;

        responseUploadDto.setHost(host);
        responseUploadDto.setAccessKeyId(accessId);
        responseUploadDto.setPolicy(encodedPolicy);
        responseUploadDto.setSignature(postSignature);
        responseUploadDto.setFilename(filename);
        responseUploadDto.setFileUrl(fileUrl);
        responseUploadDto.setSuccess(true);
        return responseUploadDto;
    }

    @Override
    public ResponseUploadDto uploadAvatar(User user) throws UnsupportedEncodingException {
        String filename = user.getId() + "_" + user.getUsername() + ".png";
        return upload(avatarDir, filename);
    }

    @Override
    public ResponseUploadDto uploadBlogImage(User user) throws UnsupportedEncodingException {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String dateString = formatter.format(currentTime);
        String filename = user.getId() + "_" + user.getUsername() + "/" + dateString + ".png";
        return upload(blogImgDir, filename);
    }

    @Override
    public ResponseUploadDto uploadBook(String filename) throws UnsupportedEncodingException {
        return upload(bookDir, filename);
    }

    @Override
    public ResponseUploadDto uploadVideo(String filename) throws UnsupportedEncodingException {
        return upload(videoDir, filename);
    }

    @Override
    public ResponseUploadDto uploadMusic(String filename) throws UnsupportedEncodingException {
        return upload(musicDir, filename);
    }

    @Override
    public ResponseUploadDto uploadPicture(String filename) throws UnsupportedEncodingException {
        return upload(pictureDir, filename);
    }

    @Override
    public ResponseUploadDto uploadFile(String type, String filename) throws UnsupportedEncodingException {
        ResponseUploadDto responseUploadDto = new ResponseUploadDto();
        responseUploadDto.setSuccess(false);
        responseUploadDto.setErrorMsg("上传类型只能为 Book Video Music Picture");
        if("Book".equalsIgnoreCase(type)){
            responseUploadDto = uploadBook(filename);
        } else if("Video".equalsIgnoreCase(type)){
            responseUploadDto = uploadVideo(filename);
        } else if("Music".equalsIgnoreCase(type)){
            responseUploadDto = uploadMusic(filename);
        } else if("Picture".equalsIgnoreCase(type)){
            responseUploadDto = uploadPicture(filename);
        }
        return responseUploadDto;
    }
}
