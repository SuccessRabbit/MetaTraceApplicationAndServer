package xyz.handsomelee.Controller;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import reactor.util.annotation.Nullable;

import java.io.File;
import java.nio.file.Paths;
import java.util.UUID;

@Controller

public class FileController {

    // 文件上传接口
    @RequestMapping(value="/upload", method=RequestMethod.POST)
    @ResponseBody
    public String upload(@RequestParam("file") MultipartFile file){

        String fileName = file.getOriginalFilename();
         //获取文件后缀名
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        // 重新生成文件名
        fileName = UUID.randomUUID() + suffixName;
        // 指定本地文件夹存储位置
        String filePathRoot = "/www/metaTraceFileSystem/";
        try{
            file.transferTo(new File(filePathRoot + fileName));
            return "http://193.203.13.134:999/" + fileName;
        }catch (Exception e){
            e.printStackTrace();
            return "failed";
        }
    }

}
