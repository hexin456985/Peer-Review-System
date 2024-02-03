package com.micro.conferencesserve.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.micro.conferencesserve.sys.entity.PaperPCMember;

public interface IPaperPCMemberService extends IService<PaperPCMember> {
    public void assignPapersByTopic(Integer topicId, Integer PCMemberId);
    public void assignPapers(Integer conferenceId);

    public void checkPaperState(Integer paperId);
}
