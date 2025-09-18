package com.digicompass.backend.infrastucture.persistence.mapper;

import com.digicompass.backend.domain.model.User;
import com.digicompass.backend.infrastucture.persistence.document.UserDocument;
import com.github.dozermapper.core.DozerBeanMapper;
import com.github.dozermapper.core.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMongoMapper {


    private static Mapper mapper;

    public UserMongoMapper(Mapper mapper) {
        this.mapper = mapper;
    }

    public static UserDocument toDocument(User user) {
        return mapper.map(user, UserDocument.class);
    }

    public static User toDomain(UserDocument document) {
        return mapper.map(document, User.class);
    }
}
