package com.example.sockApi.utils;

import com.example.sockApi.dto.SockDto;
import com.example.sockApi.entity.Sock;
import org.springframework.stereotype.Service;

@Service
public class MappingUtils {

    public SockDto mapToSockDto(Sock sockEntity) {
        SockDto sockDto = new SockDto();
        sockDto.setId(sockEntity.getId());
        sockDto.setColor(sockEntity.getColor());
        sockDto.setCottonPercentage(sockEntity.getCottonPercentage());
        sockDto.setCount(sockEntity.getCount());
        return sockDto;
    }

    public Sock mapToSock(SockDto sockDto) {
        Sock sockEntity = new Sock();
        sockEntity.setId(sockDto.getId());
        sockEntity.setColor(sockDto.getColor());
        sockEntity.setCottonPercentage(sockDto.getCottonPercentage());
        sockEntity.setCount(sockDto.getCount());
        return sockEntity;
    }

}
