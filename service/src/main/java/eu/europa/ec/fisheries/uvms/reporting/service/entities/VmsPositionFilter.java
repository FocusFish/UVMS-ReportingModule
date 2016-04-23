package eu.europa.ec.fisheries.uvms.reporting.service.entities;

import eu.europa.ec.fisheries.schema.movement.search.v1.ListCriteria;
import eu.europa.ec.fisheries.schema.movement.search.v1.RangeCriteria;
import eu.europa.ec.fisheries.schema.movement.search.v1.SearchKey;
import eu.europa.ec.fisheries.schema.movement.v1.MovementActivityTypeType;
import eu.europa.ec.fisheries.schema.movement.v1.MovementTypeType;
import eu.europa.ec.fisheries.uvms.reporting.service.mapper.VmsPositionFilterMapper;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.joda.time.DateTime;

import static java.util.Arrays.asList;

@Entity
@DiscriminatorValue("VMSPOS")
@EqualsAndHashCode(callSuper = true)
@ToString
public class VmsPositionFilter extends Filter {

    @Column(name = "MIN_SPEED")
    private Float minimumSpeed;

    @Column(name = "MAX_SPEED")
    private Float maximumSpeed;

    @Column(name = "MOV_TYPE")
    private MovementTypeType movementType;

    @Column(name = "MOV_ACTIVITY")
    private MovementActivityTypeType movementActivity;

    VmsPositionFilter() {
        super(FilterType.vmspos);
    }

    @Builder
    public VmsPositionFilter(Long id, MovementActivityTypeType movementActivity, MovementTypeType movementType,
                             Float maximumSpeed, Float minimumSpeed) {
        super(FilterType.vmspos);
        setId(id);
        this.movementActivity = movementActivity;
        this.movementType = movementType;
        this.maximumSpeed = maximumSpeed;
        this.minimumSpeed = minimumSpeed;
    }

    @Override
    public <T> T accept(FilterVisitor<T> visitor) {
        return visitor.visitVmsPositionFilter(this);
    }

    @Override
    public void merge(Filter filter) {
        VmsPositionFilterMapper.INSTANCE.merge((VmsPositionFilter) filter, this);
    }

    @Override
    public Object getUniqKey() {
        return getType();
    }

    @Override
    public List<ListCriteria> movementListCriteria() {
        List<ListCriteria> criteria = new ArrayList<>();
        ListCriteria listCriteria;

        if (movementType != null) {
            listCriteria = new ListCriteria();
            listCriteria.setKey(SearchKey.MOVEMENT_TYPE);
            listCriteria.setValue(getMovementType().name());
            criteria.add(listCriteria);
        }
        if (movementActivity != null) {
            listCriteria = new ListCriteria();
            listCriteria.setKey(SearchKey.ACTIVITY_TYPE);
            listCriteria.setValue(getMovementActivity().name());
            criteria.add(listCriteria);
        }
        return criteria;
    }

    @Override
    public List<RangeCriteria> movementRangeCriteria(DateTime now) {
        return asList(VmsPositionFilterMapper.INSTANCE.speedRangeToRangeCriteria(this));
    }

    public MovementTypeType getMovementType() {
        return movementType;
    }

    public void setMovementType(MovementTypeType movementType) {
        this.movementType = movementType;
    }

    public MovementActivityTypeType getMovementActivity() {
        return movementActivity;
    }

    public void setMovementActivity(MovementActivityTypeType movementActivity) {
        this.movementActivity = movementActivity;
    }

    public Float getMinimumSpeed() {
        return minimumSpeed;
    }

    public void setMinimumSpeed(Float minimumSpeed) {
        this.minimumSpeed = minimumSpeed;
    }

    public Float getMaximumSpeed() {
        return maximumSpeed;
    }

    public void setMaximumSpeed(Float maximumSpeed) {
        this.maximumSpeed = maximumSpeed;
    }

}
