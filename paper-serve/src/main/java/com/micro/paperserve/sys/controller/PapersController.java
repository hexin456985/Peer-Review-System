package com.micro.paperserve.sys.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.micro.common.Result;
import com.micro.common.utils.JwtUtil;

import com.micro.conferenceserve.proto.*;
import com.micro.paperPCMemberServe.proto.GetPapersInfoRequest;
import com.micro.paperPCMemberServe.proto.GetPapersInfoResponse;
import com.micro.paperPCMemberServe.proto.PaperPCMemberServiceGrpc;

import com.micro.paperserve.sys.entity.*;
import com.micro.paperserve.sys.mapper.*;
import com.micro.paperserve.sys.service.CosService;
import com.micro.paperserve.sys.service.impl.AuthorServiceImpl;
import com.micro.paperserve.sys.service.impl.PaperAuthorsServiceImpl;
import com.micro.paperserve.sys.service.impl.PapersServiceImpl;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.micro.common.Result.success;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author
 * @since 2023-11-06
 */

@Api(tags = {"论文接口列表"})
@RestController
@RequestMapping("/papers")
@Slf4j
public class PapersController {
    @Autowired
    private PapersServiceImpl papersService;

    @Autowired
    private AuthorServiceImpl authorService;

    @Resource
    private PaperAuthorsMapper paperauthorsMapper;

    @Resource
    private PapersMapper papersMapper;

    @Resource
    private AuthorMapper authorMapper;

    @Resource
    private TopicMapper topicMapper;

    @Resource
    private PaperTopicsMapper papertopicsMapper;

    @Autowired
    private PaperAuthorsServiceImpl paperauthorsService;

    @GrpcClient("conference-serve")
    PaperPCMemberServiceGrpc.PaperPCMemberServiceBlockingStub synchronousPaperPCMemberClient;

    @GrpcClient("conference-serve")
    ConferenceServiceGrpc.ConferenceServiceBlockingStub synchronousConferenceClient;

    @Resource
    private JwtUtil jwtUtil;

    AuthorController authorController = new AuthorController();
    /**
     * @param conferenceId
     * @param papertitle
     * @param paperabstract
     * @param file
     * @param topics
     * @param authorjson
     * @param token
     * @return
     */
    @PostMapping("/submit-paper/{conferenceId}")
    public Result<Map<String, Object>> submitPaper(@PathVariable Integer conferenceId,
                                                   @RequestParam("papertitle") String papertitle,
                                                   @RequestParam("paperabstract") String paperabstract,
                                                   @RequestParam("file") MultipartFile file,
                                                   @RequestParam("topic") List<Integer> topics,
                                                   @RequestParam("author") String authorjson,
                                                   String token) {
        // 检查提交者是否是chair
        getChairIdByConferenceIdRequest request = getChairIdByConferenceIdRequest.newBuilder()
                .setConferenceId(conferenceId)
                .build();

        getChairIdByConferenceIdResponse response = synchronousConferenceClient.getChairIdByConferenceId(request);
        Integer chairId = response.getChairId();
        Integer authorId = jwtUtil.getUserIdFromToken(token);
        if (chairId.equals(authorId)) {
            return Result.fail("会议主席不能投稿此会议");
        }

        log.debug("title " + papertitle);
        log.debug("abstract " + paperabstract);
        log.debug("file " + file);
        log.debug("begin");
        try {
            log.debug(file.getOriginalFilename());
            AuthorController authorController = new AuthorController();
            List<Author> authors = authorController.parseAuthorsJson(authorjson);

            Papers paper = new Papers();
            CosService cosService = new CosService();


            //=========上传pdf文件=================
            String fileName = file.getOriginalFilename();
            String r = cosService.upload(file);
            if (r != "Fail") {
                log.debug("File uploads successfully.");
                paper.setpDFPath(fileName);
            } else {
                log.debug("File uploads unsuccessfully");

            }
            //==========================================

            //==========上传paper基本信息==================
            paper.setTitle(papertitle);
            paper.setPaperAbstract(paperabstract);
            paper.setConferenceId(conferenceId);
            paper.setAuthorId(authorId);
            paper.setStatus("NotReviewed");
            papersService.save(paper);
            for (Integer topic_id : topics) {
                PaperTopics paperTopics = new PaperTopics();
                paperTopics.setPaperId(paper.getPaperId());
                paperTopics.setTopicId(topic_id);
                papertopicsMapper.insert(paperTopics);
            }
            //==========================================
            //==========上传author基本信息================
            for (Author author : authors) {
                authorMapper.insertAuthor(author);
                PaperAuthors paperauthors = new PaperAuthors();
                paperauthors.setAuthorId(author.getAuthorId());
                paperauthors.setPaperId(paper.getPaperId());
                paperauthorsService.save(paperauthors);

            }
            //====================================
        } catch (Exception e) {
            System.err.println(e);
            return Result.fail("论文上传失败");
        }
        return success("论文上传成功");
    }

    @PostMapping("/update-paper/{paper_id}")
    public Result<Map<String, Object>> updatePaper(@PathVariable Integer paper_id,
                                                   @RequestParam("papertitle") String papertitle,
                                                   @RequestParam("paperabstract") String paperabstract,
                                                   @RequestParam(value = "file", required = false) MultipartFile file,
                                                   @RequestParam("topic") List<Integer> topics,
                                                   @RequestParam("author") String authorjson,
                                                   String token) {
        try {
            Papers paper = papersMapper.getPaperWithId(paper_id);

            if (!papertitle.trim().isEmpty()) {
                paper.setTitle(papertitle);
            }
             if (!paperabstract.trim().isEmpty()) {
                paper.setPaperAbstract(paperabstract);
            }
            if (file != null) {
                paper.setpDFPath(file.getOriginalFilename());

                CosService cosService = new CosService();
   
                String r = cosService.upload(file);
                if (r != "Fail") {
                    log.debug("File uploads successfully.");
                } else {
                    log.debug("File uploads unsuccessfully");

                }
            }
            
            List<Author> origin_authors = authorMapper.getAuthorsByPaperId(paper_id);
            if (origin_authors.size()!= 0){
                Integer flag_author = 0;
                if (authorjson != null && !authorjson.isEmpty()) {
                List<Author> authors = authorController.parseAuthorsJson(authorjson);
                for (Author origin_author : origin_authors) {
                    origin_author.setAuthorId(null);
                }
                for (Integer i =0; i<authors.size(); i++) {
                    if (!authors.get(i).toString().equals(origin_authors.get(i).toString())) {
                        paperauthorsMapper.deleteByPaperId(paper_id);
                        flag_author = 1;
                        break;
                    }
                }
                if (flag_author==1){
                for (Author author : authors) {
                    authorMapper.insertAuthor(author);
                    System.out.println(author.getAuthorId());
                    PaperAuthors paperauthors = new PaperAuthors();
                    paperauthors.setAuthorId(author.getAuthorId());
                    paperauthors.setPaperId(paper.getPaperId());
                    paperauthorsService.save(paperauthors);
                }
                }
            }

            }
            
             
            List<Integer> origin_topics = papertopicsMapper.getByPaperId(paper_id);
            if (origin_topics.size()!=0) {
                if (!topics.isEmpty()){
                    if (!origin_topics.equals(topics)){
                        papertopicsMapper.deleteByPaperId(paper_id);
                         if (!topics.isEmpty()) {
                            for (Integer topic_id : topics) {
                            PaperTopics paperTopics = new PaperTopics();
                            paperTopics.setPaperId(paper.getPaperId());
                            paperTopics.setTopicId(topic_id);
                            papertopicsMapper.insert(paperTopics);
                }
            }
                    }
                }
            }
    
            
            papersService.updateById(paper);
            

        } catch (Exception e) {
            System.err.println(e);
            return Result.fail("论文更新失败");
        }
        return Result.success("论文更新成功");
    }


//    /*
//     * 获取指定论文
//     */
//    @GetMapping("/{paper_id,}")
//    public Result<Map<String, Object>> getPaperById(@PathVariable Integer paper_id) {
//        Papers papers = papersService.getById(paper_id);
//    }


    /*
     * 获取当前用户上传的论文列表
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> getPaperList(
            @RequestParam(value = "pageNo") Long pageNo,
            @RequestParam(value = "pageSize") Long pageSize,
            String token
    ) {
        Integer authorId = jwtUtil.getUserIdFromToken(token);
        LambdaQueryWrapper<Papers> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Papers::getAuthorId, authorId); // 根据状态筛选
        Page<Papers> page = new Page<>(pageNo, pageSize);
        papersService.page(page, wrapper);

        Map<String, Object> data = new HashMap<>();
        data.put("total", page.getTotal());
        data.put("rows", page.getRecords());

        return Result.success(data);
    }

    /*
     * 返回一个user其参与的所有会议的所有论文
     */
    @GetMapping("/assignment-papers/show")
    public Result<Map<String, Object>> getAssignmentPapers(@RequestParam("conferenceIds") List<Integer> conferenceIds,
                                                            @RequestParam("token") String token,
                                                            @RequestParam(value = "pageNo") Long pageNo,
                                                            @RequestParam(value = "pageSize") Long pageSize) {
        log.debug("accept param:" + conferenceIds.toString() + " " + token);
        Integer uid = jwtUtil.getUserIdFromToken(token);
        Map<Papers, Integer> map = new HashMap<>();
        List<Integer> papersIds = new ArrayList<>();

        for (Integer conferenceId: conferenceIds) {
            GetPapersInfoRequest grequest = GetPapersInfoRequest.newBuilder()
                    .setUserId(uid)
                    .setConferenceId(conferenceId)
                    .build();

            GetPapersInfoResponse response = synchronousPaperPCMemberClient.getPapersInfo(grequest);
            List<Integer> papersId = response.getPaperIdList();
            // List<Integer> states = response.getStateList();
//            for(int i = 0; i < papersId.size(); i++){
//                map.put(papersService.getById(papersId.get(i)), states.get(i));
//            }
            if (papersIds.isEmpty()) {
                papersIds = new ArrayList<>(papersId);
            } else {
                papersIds.addAll(papersId);
            }
        }
        QueryWrapper<Papers> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("paper_id", papersIds);

        Page<Papers> page = new Page<>(pageNo, pageSize);
        papersService.page(page, queryWrapper);

        Map<String, Object> data = new HashMap<>();
        data.put("total", page.getTotal());
        data.put("rows", page.getRecords());

        return Result.success(data);
    }



    @GetMapping("/assignment-papers/download")
    public Result<Map<String, Object>> downloadAssignmentPaper(@RequestParam("paperId") Integer paperId,
                                                               @RequestParam("localFilePath") String localFilePath) {
        log.debug("paperId" + paperId);
        log.debug("localFilePath" + localFilePath);
        try {
            CosService cosService = new CosService();
            String fileName = papersService.getById(paperId).getpDFPath();

            // 改成返回URL给前端，后端自己不下载
            URL returnUrl = cosService.generatePresignedUrl(fileName);
//            String r = cosService.download(fileName, localFilePath);
//
//            if(!r.equals("Fail")) {
//                log.debug("download successfully");
//            } else {
//                log.debug("download unsuccessfully");
//            }
            Map<String, Object> data = new HashMap<>();
            data.put("URL", returnUrl);
            return Result.success(data);
        } catch (Exception e) {
            System.err.println(e);
            return Result.fail("下载失败");
        }
    }
}







