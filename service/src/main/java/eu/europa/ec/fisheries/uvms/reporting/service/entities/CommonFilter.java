package eu.europa.ec.fisheries.uvms.reporting.service.entities;

import com.google.common.collect.Lists;
import eu.europa.ec.fisheries.schema.movement.search.v1.ListCriteria;
import eu.europa.ec.fisheries.schema.movement.search.v1.RangeCriteria;
import eu.europa.ec.fisheries.schema.movement.search.v1.RangeKeyType;
import eu.europa.ec.fisheries.uvms.common.DateUtils;
import eu.europa.ec.fisheries.uvms.reporting.service.dto.FilterDTO;
import eu.europa.ec.fisheries.uvms.reporting.service.mapper.DateTimeFilterMapper;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.NotImplementedException;
import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

@Entity
@DiscriminatorValue("DATETIME")
@EqualsAndHashCode(callSuper = true, of = {"startDate", "endDate"})
public class CommonFilter extends Filter {

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "START_DATE")
    private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "END_DATE")
    private Date endDate;

    @Embedded
    private PositionSelector positionSelector;

    public CommonFilter(){
        super(FilterType.common);
    }

    @Builder(builderMethodName = "CommonFilterBuilder")
    public CommonFilter(final Long id, final Date startDate, final Date endDate, final PositionSelector positionSelector) {
        super(FilterType.common);
        setStartDate(startDate);
        setEndDate(endDate);
        setPositionSelector(positionSelector);
        setId(id);
    }

    @Override
    public FilterDTO convertToDTO() {
        return DateTimeFilterMapper.INSTANCE.dateTimeFilterToDateTimeFilterDTO(this);
    }

    @Override
    public void merge(Filter filter) {
        CommonFilter incoming = (CommonFilter) filter;
        setEndDate(incoming.getEndDate());
        setStartDate(incoming.getStartDate());
    }

    @Override
    public List<RangeCriteria> movementRangeCriteria(){

        RangeCriteria criteria = new RangeCriteria();

        switch (positionSelector.getSelector()){

            case all:
                setDateCriteria(criteria, startDate, endDate);
                break;

            case last:

                Position position = positionSelector.getPosition();

                switch (position){

                    case hours:
                        Float hours = getPositionSelector().getValue();
                        DateTime currentDate = nowUTC();
                        Date toDate = DateUtils.nowUTCMinusSeconds(currentDate, hours).toDate();
                        setDateCriteria(criteria, toDate, currentDate.toDate());
                        break;

                    case positions:

                        break;

                    default:

                }

                break;
            default:

        }
        return Lists.newArrayList(criteria);
    }

    private void setDateCriteria(final RangeCriteria criteria, final Date to, final Date from) {
        criteria.setKey(RangeKeyType.DATE);
        criteria.setFrom(DateUtils.dateToString(to));
        criteria.setTo(DateUtils.dateToString(from));
    }

    // UT
    protected DateTime nowUTC() {
        return DateUtils.nowUTC();
    }

    @Override
    public Object getUniqKey() {
        return getId();
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        if (startDate != null){
            this.startDate = new Date(startDate.getTime());
        }
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        if (endDate != null){
            this.endDate = new Date(endDate.getTime());
        }
    }

    public PositionSelector getPositionSelector() {
        return positionSelector;
    }

    public void setPositionSelector(PositionSelector positionSelector) {
        this.positionSelector = positionSelector;
    }

}
