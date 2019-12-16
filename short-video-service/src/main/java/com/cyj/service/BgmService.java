package com.cyj.service;

import com.cyj.pojo.Bgm;

import java.util.List;

public interface BgmService {
    List<Bgm> queryBgmList();

    Bgm queryBgmById(String bgmId);

}
