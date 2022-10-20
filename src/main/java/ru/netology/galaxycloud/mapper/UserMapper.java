package ru.netology.galaxycloud.mapper;

import org.mapstruct.Mapper;
import ru.netology.galaxycloud.dto.UserDto;
import ru.netology.galaxycloud.entities.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User userDtoToUser(UserDto userDto);

    UserDto userToUserDto(User user);

}
