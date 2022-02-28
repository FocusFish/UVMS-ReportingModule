package fish.focus.uvms.reporting.service.entities.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import org.apache.commons.lang3.StringUtils;
import fish.focus.uvms.reporting.service.enums.GroupCriteriaType;
import fish.focus.uvms.reporting.service.mapper.GroupCriteriaFilterMapper;

@Converter(autoApply=true)
public class GroupCriteriaFilterConverter implements AttributeConverter<List, String> {

    @Override
    public String convertToDatabaseColumn(List groupCriteriaTypes) {
        List<String> strings = GroupCriteriaFilterMapper.INSTANCE.mapStringListToGroupCriteriaTypeList(groupCriteriaTypes);
        return StringUtils.join(strings, ",");
    }

    @Override
    public List<GroupCriteriaType> convertToEntityAttribute(String dbData) {
        if (StringUtils.isBlank(dbData)) {
            return new ArrayList<>();
        }
        List <String> strings = Arrays.asList(dbData.split(","));
        return GroupCriteriaFilterMapper.INSTANCE.mapGroupCriteriaTypeListToStringList(strings);
    }
}
