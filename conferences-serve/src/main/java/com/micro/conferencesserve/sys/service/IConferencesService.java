package com.micro.conferencesserve.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.micro.conferencesserve.sys.entity.Conferences;

import java.util.List;


/**
 * <p>
 *  服务类
 * </p>
 * @author 
 * @since 2023-10-24
 */
public interface IConferencesService extends IService<Conferences> {
    public boolean isNameAvailable(String name);
    
    public void saveConference(Conferences conferences);

    public List<Conferences> getConferencesByStatus();

    public void updateConferenceStatus(Integer conferenceID, String action);

    public Conferences getConferenceById(Integer conferenceID);

    public List<Conferences> getAllConferences();

//    public List<Integer> getConferencesIdByChairId(Integer chairId);

    public Integer getLastInsertId();
}

