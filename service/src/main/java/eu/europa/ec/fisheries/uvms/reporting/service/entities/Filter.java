package eu.europa.ec.fisheries.uvms.reporting.service.entities;

import eu.europa.ec.fisheries.schema.movement.search.v1.ListCriteria;
import eu.europa.ec.fisheries.schema.movement.search.v1.RangeCriteria;
import eu.europa.ec.fisheries.schema.movement.search.v1.SearchKey;
import eu.europa.ec.fisheries.uvms.reporting.service.dto.FilterDTO;
import eu.europa.ec.fisheries.wsdl.vessel.group.VesselGroup;
import eu.europa.ec.fisheries.wsdl.vessel.types.VesselListCriteriaPair;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "filter", schema = "reporting")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="FILTER_TYPE")
@NamedQueries({
        @NamedQuery(name = Filter.LIST_BY_REPORT_ID, query = "SELECT f FROM Filter f WHERE report.id = :reportId"),
        @NamedQuery(name = Filter.DELETE_BY_ID, query = "DELETE FROM Filter WHERE id = :id")
})
@EqualsAndHashCode(of = {"id"})
public abstract class Filter implements Serializable {

    public static final String LIST_BY_REPORT_ID = "Filter.listByReportId";
    public static final String DELETE_BY_ID = "Filter.deleteById";

    @Id
    @Column(name = "filter_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Transient
    final private FilterType type;

    public Filter(FilterType type) { this.type = type; }

    @ManyToOne(cascade = javax.persistence.CascadeType.ALL)
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    @Transient
    private Long reportId;

    public abstract FilterDTO convertToDTO();

    public abstract void merge(Filter filter);

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FilterType getType() {
        return type;
    }

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public abstract Object getUniqKey();

    //Hook me
    public VesselListCriteriaPair vesselCriteria() {
        return new VesselListCriteriaPair(); // FIXME try with null
    }

    //Hook me
    public ListCriteria movementCriteria() {
        return new ListCriteria(); // FIXME try with null
    }

    //Hook me
    public VesselGroup vesselGroupCriteria(){
        return new VesselGroup(); // FIXME try with null
    }

    //Hook me
    public RangeCriteria movementRangeCriteria(){
        return new RangeCriteria(); // FIXME try with null
    }
}
