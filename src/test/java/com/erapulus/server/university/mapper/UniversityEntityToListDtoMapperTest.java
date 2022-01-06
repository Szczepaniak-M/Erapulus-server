package com.erapulus.server.university.mapper;

import com.erapulus.server.TestUtils;
import com.erapulus.server.university.database.UniversityEntity;
import com.erapulus.server.university.dto.UniversityListDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UniversityEntityToListDtoMapperTest {

    private static final int ID = 1;
    private static final String NAME = "name";
    private static final String ADDRESS = "address";
    private static final String ADDRESS_2 = "address2";
    private static final String CITY = "city";
    private static final String COUNTRY = "country";
    private static final String DESCRIPTION = "description";
    private static final String ZIPCODE = "00-000";
    private static final String WEBSITE_URL = "https://www.example.com/1";
    private static final String LOGO_URL = "https://www.example.com/2";

    @Test
    void from_shouldMapEntityToDto() {
        // given
        UniversityEntity entity = UniversityEntity.builder()
                                                  .id(ID)
                                                  .name(NAME)
                                                  .address(ADDRESS)
                                                  .address2(ADDRESS_2)
                                                  .city(CITY)
                                                  .country(COUNTRY)
                                                  .description(DESCRIPTION)
                                                  .zipcode(ZIPCODE)
                                                  .websiteUrl(WEBSITE_URL)
                                                  .logoUrl(LOGO_URL)
                                                  .build();

        // when
        UniversityListDto result = new UniversityEntityToListDtoMapper().from(entity);

        //then
        assertEquals(ID, result.id());
        assertEquals(NAME, result.name());
        assertEquals(LOGO_URL, result.logoUrl());
        assertTrue(TestUtils.createValidator().validate(result).isEmpty());
    }
}