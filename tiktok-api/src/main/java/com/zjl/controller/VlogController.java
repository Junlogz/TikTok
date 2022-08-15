package com.zjl.controller;

import com.zjl.base.BaseInfoProperties;
import com.zjl.bo.VlogBO;
import com.zjl.enums.YesOrNo;
import com.zjl.grace.result.GraceJSONResult;
import com.zjl.service.VlogService;
import com.zjl.util.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@Api(tags = "VlogController 短视频相关业务功能的接口")
@RequestMapping("vlog")
@RestController
public class VlogController extends BaseInfoProperties {

    @Autowired
    private VlogService vlogService;

    @ApiOperation("视频发布")
    @PostMapping("publish")
    public GraceJSONResult publish(@Valid @RequestBody VlogBO vlogBO) {
        vlogService.createVlog(vlogBO);
        return GraceJSONResult.ok();
    }

    @ApiOperation("视频查询")
    @GetMapping("indexList")
    public GraceJSONResult indexList(@RequestParam(defaultValue = "") String userId,
                                     @RequestParam(defaultValue = "") String search,
                                     @RequestParam Integer page,
                                     @RequestParam Integer pageSize) {
        if (page == null) {
            page = COMMON_START_PAGE;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult gridResult = vlogService.getIndexVlogList(userId, search, page, pageSize);
        return GraceJSONResult.ok(gridResult);
    }

//    @ApiOperation("视频查询")
//    @GetMapping("indexList")
//    public GraceJSONResult indexList(@RequestParam(defaultValue = "") String userId,
//                                     @RequestParam(defaultValue = "") String search,
//                                     @RequestParam Integer page,
//                                     @RequestParam Integer pageSize) {
//
//        if (page == null) {
//            page = COMMON_START_PAGE;
//        }
//        if (pageSize == null) {
//            pageSize = COMMON_PAGE_SIZE;
//        }
//
//        PagedGridResult gridResult = vlogService.getIndexVlogList(userId, search, page, pageSize);
//        return GraceJSONResult.ok(gridResult);
//    }


    @ApiOperation("根据主键查询vlog详细")
    @GetMapping("detail")
    public GraceJSONResult detail(@RequestParam(defaultValue = "") String userId,
                                  @RequestParam String vlogId) {
        return GraceJSONResult.ok(vlogService.getVlogDetailById(vlogId));
    }

    @ApiOperation("视频设为私密")
    @PostMapping("changeToPrivate")
    public GraceJSONResult changeToPrivate(@RequestParam String userId,
                                           @RequestParam String vlogId) {
        vlogService.changeToPrivateOrPublic(userId,
                                            vlogId,
                                            YesOrNo.YES.type);
        return GraceJSONResult.ok();
    }

    @ApiOperation("视频设为公开")
    @PostMapping("changeToPublic")
    public GraceJSONResult changeToPublic(@RequestParam String userId,
                                           @RequestParam String vlogId) {
        vlogService.changeToPrivateOrPublic(userId,
                                            vlogId,
                                            YesOrNo.NO.type);
        return GraceJSONResult.ok();
    }

    @ApiOperation("我的公开视频列表")
    @GetMapping("myPublicList")
    public GraceJSONResult myPublicList(@RequestParam String userId,
                                     @RequestParam Integer page,
                                     @RequestParam Integer pageSize) {

        if (page == null) {
            page = COMMON_START_PAGE;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult gridResult = vlogService.queryMyVlogList(userId,
                                                                page,
                                                                pageSize,
                                                                YesOrNo.NO.type);
        return GraceJSONResult.ok(gridResult);
    }

    @ApiOperation("我的私密视频列表")
    @GetMapping("myPrivateList")
    public GraceJSONResult myPrivateList(@RequestParam String userId,
                                        @RequestParam Integer page,
                                        @RequestParam Integer pageSize) {

        if (page == null) {
            page = COMMON_START_PAGE;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult gridResult = vlogService.queryMyVlogList(userId,
                                                                page,
                                                                pageSize,
                                                                YesOrNo.YES.type);
        return GraceJSONResult.ok(gridResult);
    }

//
//    @GetMapping("myLikedList")
//    public GraceJSONResult myLikedList(@RequestParam String userId,
//                                         @RequestParam Integer page,
//                                         @RequestParam Integer pageSize) {
//
//        if (page == null) {
//            page = COMMON_START_PAGE;
//        }
//        if (pageSize == null) {
//            pageSize = COMMON_PAGE_SIZE;
//        }
//
//        PagedGridResult gridResult = vlogService.getMyLikedVlogList(userId,
//                                                                    page,
//                                                                    pageSize);
//        return GraceJSONResult.ok(gridResult);
//    }
//
//
    @PostMapping("like")
    public GraceJSONResult like(@RequestParam String userId,
                                 @RequestParam String vlogerId,
                                 @RequestParam String vlogId) {

        // 我点赞的视频，关联关系保存到数据库
        vlogService.userLikeVlog(userId, vlogId);

        // 点赞后，视频和视频发布者的获赞都会 +1
        redis.increment(REDIS_VLOGER_BE_LIKED_COUNTS + ":" + vlogerId, 1);
        redis.increment(REDIS_VLOG_BE_LIKED_COUNTS + ":" + vlogId, 1);

        // 我点赞的视频，需要在redis中保存关联关系
        redis.set(REDIS_USER_LIKE_VLOG + ":" + userId + ":" + vlogId, "1");

        return GraceJSONResult.ok();
    }


    @PostMapping("unlike")
    public GraceJSONResult unlike(@RequestParam String userId,
                                @RequestParam String vlogerId,
                                @RequestParam String vlogId) {

        // 我取消点赞的视频，关联关系删除
        vlogService.userUnLikeVlog(userId, vlogId);

        redis.decrement(REDIS_VLOGER_BE_LIKED_COUNTS + ":" + vlogerId, 1);
        redis.decrement(REDIS_VLOG_BE_LIKED_COUNTS + ":" + vlogId, 1);
        redis.del(REDIS_USER_LIKE_VLOG + ":" + userId + ":" + vlogId);

        return GraceJSONResult.ok();
    }
//
//    @PostMapping("totalLikedCounts")
//    public GraceJSONResult totalLikedCounts(@RequestParam String vlogId) {
//        return GraceJSONResult.ok(vlogService.getVlogBeLikedCounts(vlogId));
//    }
//
//    @GetMapping("followList")
//    public GraceJSONResult followList(@RequestParam String myId,
//                                       @RequestParam Integer page,
//                                       @RequestParam Integer pageSize) {
//
//        if (page == null) {
//            page = COMMON_START_PAGE;
//        }
//        if (pageSize == null) {
//            pageSize = COMMON_PAGE_SIZE;
//        }
//
//        PagedGridResult gridResult = vlogService.getMyFollowVlogList(myId,
//                                                                    page,
//                                                                    pageSize);
//        return GraceJSONResult.ok(gridResult);
//    }
//
//    @GetMapping("friendList")
//    public GraceJSONResult friendList(@RequestParam String myId,
//                                      @RequestParam Integer page,
//                                      @RequestParam Integer pageSize) {
//
//        if (page == null) {
//            page = COMMON_START_PAGE;
//        }
//        if (pageSize == null) {
//            pageSize = COMMON_PAGE_SIZE;
//        }
//
//        PagedGridResult gridResult = vlogService.getMyFriendVlogList(myId,
//                                                                    page,
//                                                                    pageSize);
//        return GraceJSONResult.ok(gridResult);
//    }
}
