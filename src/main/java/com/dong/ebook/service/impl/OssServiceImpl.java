package com.dong.ebook.service.impl;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.dong.ebook.common.UploadFileType;
import com.dong.ebook.dto.ResponseUploadDto;
import com.dong.ebook.model.User;
import com.dong.ebook.security.AuthUserService;
import com.dong.ebook.service.OssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class OssServiceImpl implements OssService {
    String accessId = "LTAIusAuN2Y14710";
    String accessKey = "FdoUn7i66qif3mJk8IONY7TFL4p9UO";
    String endpoint = "oss-cn-beijing.aliyuncs.com";
    String bucket = "e-bookpublic"; //  bucketname
    String host = "http://" + bucket + "." + endpoint; // host的格式为 bucketname.endpoint
    String avatarDir = "avatar/"; // 用户上传文件时指定的前缀 如：user-avatarDir-prefix/
    String blogImgDir = "blogImg/";
    String bookDir = "book/";
    String bookCoverDir = "bookCover/";
    String videoDir = "video/";
    String videoCoverDir = "videoCover/";
    String musicDir = "music/";
    String musicCoverDir = "musicCover/";
    String pictureDir = "picture/";

    @Autowired
    AuthUserService authUserService;

    @Override
    public ResponseUploadDto uploadAvatar() throws UnsupportedEncodingException {
        User user = authUserService.getCurUser();
        String filename = user.getId() + ".png";
        return upload(avatarDir, filename);
    }

    @Override
    public ResponseUploadDto uploadBlogImage() throws UnsupportedEncodingException {
        String filename = buildFilenameByUserTime(".png");
        return upload(blogImgDir, filename);
    }

    @Override
    public ResponseUploadDto uploadBook(String filename) throws UnsupportedEncodingException {
        return uploadFile(filename, UploadFileType.BOOK);
    }

    @Override
    public ResponseUploadDto uploadVideo(String filename) throws UnsupportedEncodingException {
        return uploadFile(filename, UploadFileType.VIDEO);
    }

    @Override
    public ResponseUploadDto uploadMusic(String filename) throws UnsupportedEncodingException {
        return uploadFile(filename, UploadFileType.MUSIC);
    }

    @Override
    public ResponseUploadDto uploadPicture(String filename) throws UnsupportedEncodingException {
        return uploadFile(filename, UploadFileType.PICTURE);
    }

    @Override
    public ResponseUploadDto uploadBookCover(String filename) throws UnsupportedEncodingException {
        return uploadFile(filename, UploadFileType.BOOKCOVER);
    }

    @Override
    public ResponseUploadDto uploadVideoCover(String filename) throws UnsupportedEncodingException {
        return uploadFile(filename, UploadFileType.VIDEOCOVER);
    }

    @Override
    public ResponseUploadDto uploadMusicCover(String filename) throws UnsupportedEncodingException {
        return uploadFile(filename, UploadFileType.MUSICCOVER);
    }

    public ResponseUploadDto uploadFile(String filename, String fileType) throws UnsupportedEncodingException {
        ResponseUploadDto responseUploadDto = new ResponseUploadDto();
        responseUploadDto.setSuccess(false);
        if(filename == null || filename.isEmpty()){
            responseUploadDto.setErrorMsg("文件名为空");
            return responseUploadDto;
        }
        String suffix = splitSuffix(filename);
        boolean checkRes;
        String fileDir;
        if(UploadFileType.BOOK.equals(fileType)){
            checkRes = checkBook(suffix);
            fileDir = bookDir;
        } else if(UploadFileType.VIDEO.equals(fileType)){
            checkRes = checkVideo(suffix);
            fileDir = videoDir;
        } else if(UploadFileType.MUSIC.equals(fileType)){
            checkRes = checkMusic(suffix);
            fileDir = musicDir;
        } else if(UploadFileType.PICTURE.equals(fileType)){
            checkRes = checkPicture(suffix);
            fileDir = pictureDir;
            // 批量上传会出现秒级文件名重复问题，所以后缀以filename结尾
            suffix = "_" + filename;
        } else if(UploadFileType.BOOKCOVER.equals(fileType)){
            checkRes = checkPicture(suffix);
            fileDir = bookCoverDir;
        } else if(UploadFileType.VIDEOCOVER.equals(fileType)){
            checkRes = checkPicture(suffix);
            fileDir = videoCoverDir;
        } else if(UploadFileType.MUSICCOVER.equals(fileType)){
            checkRes = checkPicture(suffix);
            fileDir = musicCoverDir;
        } else {
            responseUploadDto.setErrorMsg("不支持该格式");
            return responseUploadDto;
        }
        if(!checkRes){
            responseUploadDto.setErrorMsg("不支持该格式");
            return responseUploadDto;
        }
        filename = buildFilenameByUserTime(suffix);
        return upload(fileDir, filename);
    }

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
        String fileUrl = host + "/" + filename;

        responseUploadDto.setHost(host);
        responseUploadDto.setAccessKeyId(accessId);
        responseUploadDto.setPolicy(encodedPolicy);
        responseUploadDto.setSignature(postSignature);
        responseUploadDto.setFilename(filename);
        responseUploadDto.setFileUrl(fileUrl);
        responseUploadDto.setSuccess(true);
        return responseUploadDto;
    }

    /**
     * 生成  userId_username/2019-10-10_00:00:00.suffix
     *
     * @param suffix
     * @return
     */
    public String buildFilenameByUserTime(String suffix) {
        User user = authUserService.getCurUser();
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String dateString = formatter.format(currentTime);
        return user.getId() + "/" + dateString + suffix;
    }

    /**
     * 提取filename的后缀
     *
     * @param filename
     */
    public String splitSuffix(String filename) {
        String[] split = filename.split("\\.");
        if (split.length <= 1) {
            throw new RuntimeException("切分" + filename + "Error");
        }
        return "." + split[split.length - 1];
    }

    public boolean checkBook(String suffix) {
        if (".exe".equalsIgnoreCase(suffix) || ".txt".equalsIgnoreCase(suffix) ||
                ".html".equalsIgnoreCase(suffix) || ".caj".equalsIgnoreCase(suffix) ||
                ".chm".equalsIgnoreCase(suffix) || ".pdf".equalsIgnoreCase(suffix) ||
                ".umd".equalsIgnoreCase(suffix) || ".jar".equalsIgnoreCase(suffix)) {
            return true;
        }
        return false;
    }

    public boolean checkVideo(String suffix) {
        if (".avi".equalsIgnoreCase(suffix) || ".wmv".equalsIgnoreCase(suffix) ||
                ".mpeg".equalsIgnoreCase(suffix) || ".mp4".equalsIgnoreCase(suffix) ||
                ".mov".equalsIgnoreCase(suffix) || ".mkv".equalsIgnoreCase(suffix) ||
                ".flv".equalsIgnoreCase(suffix) || ".f4v".equalsIgnoreCase(suffix) ||
                ".m4v".equalsIgnoreCase(suffix) || ".rmvb".equalsIgnoreCase(suffix) ||
                ".rm".equalsIgnoreCase(suffix) || ".3gp".equalsIgnoreCase(suffix) ||
                ".dat".equalsIgnoreCase(suffix) || ".ts".equalsIgnoreCase(suffix) ||
                ".mts".equalsIgnoreCase(suffix) || ".vob".equalsIgnoreCase(suffix)) {
            return true;
        }
        return false;
    }

    public boolean checkMusic(String suffix) {
        if (".wav".equalsIgnoreCase(suffix) || ".mp3".equalsIgnoreCase(suffix) ||
                ".wmv".equalsIgnoreCase(suffix) || ".au".equalsIgnoreCase(suffix) ||
                ".mov".equalsIgnoreCase(suffix) || ".mkv".equalsIgnoreCase(suffix) ||
                ".aiff".equalsIgnoreCase(suffix) || ".vqf".equalsIgnoreCase(suffix) ||
                ".cd".equalsIgnoreCase(suffix) || ".ape".equalsIgnoreCase(suffix) ||
                ".midi".equalsIgnoreCase(suffix)) {
            return true;
        }
        return false;
    }

    public boolean checkPicture(String suffix) {
        if (".jpeg".equalsIgnoreCase(suffix) || ".jpg".equalsIgnoreCase(suffix) ||
                ".png".equalsIgnoreCase(suffix) || ".gif".equalsIgnoreCase(suffix) ||
                ".bmp".equalsIgnoreCase(suffix)) {
            return true;
        }
        return false;
    }
}
