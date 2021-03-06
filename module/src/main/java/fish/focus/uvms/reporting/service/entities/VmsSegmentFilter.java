/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */

package fish.focus.uvms.reporting.service.entities;

import fish.focus.schema.movement.search.v1.ListCriteria;
import fish.focus.schema.movement.search.v1.RangeCriteria;
import fish.focus.schema.movement.v1.SegmentCategoryType;
import fish.focus.uvms.reporting.service.mapper.VmsSegmentFilterMapper;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

@Entity
@DiscriminatorValue("VMSSEG")
@EqualsAndHashCode(callSuper = true)
@ToString
public class VmsSegmentFilter extends Filter {

    @Column(name = "MIN_SPEED")
    private Float minimumSpeed;

    @Column(name = "MAX_SPEED")
    private Float maximumSpeed;

    @Embedded
    private DurationRange durationRange;

    @Column(name = "SEG_CATEGORY")
    private SegmentCategoryType category;

    public VmsSegmentFilter() {
        super(FilterType.vmsseg);
    }

    @Builder
    public VmsSegmentFilter(DurationRange durationRange, Float maximumSpeed, Float minimumSpeed,
                            SegmentCategoryType category) {
        super(FilterType.vmsseg);
        this.durationRange = durationRange;
        this.maximumSpeed = maximumSpeed;
        this.minimumSpeed = minimumSpeed;
        this.category = category;
    }

    @Override
    public <T> T accept(FilterVisitor<T> visitor) {
        return visitor.visitVmsSegmentFilter(this);
    }

    @Override
    public void merge(Filter filter) {
        VmsSegmentFilterMapper.INSTANCE.merge((VmsSegmentFilter) filter, this);
    }

    @Override
    public Object getUniqKey() {
        return getType();
    }

    @Override
    public List<ListCriteria> movementListCriteria() {
        ListCriteria listCriteria = VmsSegmentFilterMapper.INSTANCE.categoryToListCriteria(this);
        List<ListCriteria> result = emptyList();
        if (listCriteria != null && listCriteria.getValue() != null) {
            result = singletonList(VmsSegmentFilterMapper.INSTANCE.categoryToListCriteria(this));
        }
        return result;
    }

    @Override
    public List<RangeCriteria> movementRangeCriteria(Instant now) {
        List<RangeCriteria> criteria = new ArrayList<>();
        if (minimumSpeed != null && maximumSpeed != null) {
            criteria.add(VmsSegmentFilterMapper.INSTANCE.speedRangeToRangeCriteria(this));
        }
        if (durationRange != null) {
            criteria.add(VmsSegmentFilterMapper.INSTANCE.durationRangeToRangeCriteria(this));
        }
        return criteria;
    }

    public SegmentCategoryType getCategory() {
        return category;
    }

    public void setCategory(SegmentCategoryType category) {
        this.category = category;
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

    public DurationRange getDurationRange() {
        return durationRange;
    }

    public void setDurationRange(DurationRange durationRange) {
        this.durationRange = durationRange;
    }

    public String convertedMaxDuration(){
        Long maxDuration = durationRange.getMaxDuration() != null ? durationRange.getMaxDuration().longValue() : Long.MAX_VALUE;
        return String.valueOf(TimeUnit.MILLISECONDS.convert(maxDuration, TimeUnit.HOURS));
    }

    public String convertedMinDuration(){
        Long minDuration = durationRange.getMinDuration() != null ? durationRange.getMinDuration().longValue() : 0;
        return String.valueOf(TimeUnit.MILLISECONDS.convert(minDuration, TimeUnit.HOURS));
    }
}