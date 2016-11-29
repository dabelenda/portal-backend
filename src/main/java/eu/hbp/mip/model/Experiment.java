package eu.hbp.mip.model;

import com.google.gson.*;
import com.google.gson.annotations.Expose;
import eu.hbp.mip.messages.external.*;
import eu.hbp.mip.messages.external.Algorithm;
import eu.hbp.mip.messages.external.ExperimentQuery;
import org.apache.log4j.Logger;
import org.hibernate.annotations.Cascade;
import scala.collection.JavaConverters;
import scala.collection.Seq;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;


/**
 * Created by habfast on 21/04/16.
 */
@Entity
@Table(name = "`experiment`")
public class Experiment {

    private static final Logger LOGGER = Logger.getLogger(Experiment.class);

    private static final Gson gson = new Gson();

    private static final Gson gsonOnlyExposed = new GsonBuilder()
            .serializeNulls()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    @Id
    @Column(columnDefinition = "uuid")
    @org.hibernate.annotations.Type(type="pg-uuid")
    @Expose
    private UUID uuid;

    @Column(columnDefinition="TEXT")
    @Expose
    private String name;

    @Expose
    @ManyToOne
    @JoinColumn(name = "createdby_username")
    private User createdBy;

    @ManyToOne
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @Expose
    private Model model;

    @Column(columnDefinition="TEXT")
    @Expose
    private String algorithms;

    @Column(columnDefinition="TEXT")
    @Expose
    private String validations;

    @Column(columnDefinition="TEXT")
    @Expose
    private String result;

    @Expose
    private Date created = new Date();

    @Expose
    private Date finished;

    @Expose
    private boolean hasError = false;

    @Expose
    private boolean hasServerError = false;

    @Expose
    private boolean shared = false;

    // whether or not the experiment's result have been resultsViewed by its owner
    @Expose
    private boolean resultsViewed = false;

    public Experiment() {
        /*
        *  Empty constructor is needed by Hibernate
        */
    }

    public ExperimentQuery computeQuery() {
        List<VariableId> variables = new LinkedList<>();
        List<VariableId> covariables = new LinkedList<>();
        List<VariableId> grouping = new LinkedList<>();
        List<Filter> filters = new LinkedList<>();
        List<eu.hbp.mip.messages.external.Algorithm> algorithms = new LinkedList<>();
        List<Validation> validations = new LinkedList<>();

        for (Variable v: model.getQuery().getVariables()
                ) {
            variables.add(new VariableId(v.getCode()));
        }

        for (Variable v: model.getQuery().getCovariables()
             ) {
            covariables.add(new VariableId(v.getCode()));
        }

        for (Variable v: model.getQuery().getGrouping()
                ) {
            grouping.add(new VariableId(v.getCode()));
        }

        // TODO: convert algorithms and so on

        Seq<VariableId> variablesSeq = JavaConverters.asScalaIteratorConverter(variables.iterator()).asScala().toSeq();
        Seq<VariableId> covariablesSeq = JavaConverters.asScalaIteratorConverter(covariables.iterator()).asScala().toSeq();
        Seq<VariableId> groupingSeq = JavaConverters.asScalaIteratorConverter(grouping.iterator()).asScala().toSeq();
        Seq<Filter> filtersSeq = JavaConverters.asScalaIteratorConverter(filters.iterator()).asScala().toSeq();
        Seq<Algorithm> algorithmsSeq = JavaConverters.asScalaIteratorConverter(algorithms.iterator()).asScala().toSeq();
        Seq<Validation> validationsSeq = JavaConverters.asScalaIteratorConverter(validations.iterator()).asScala().toSeq();

        return new ExperimentQuery(variablesSeq, covariablesSeq, groupingSeq, filtersSeq, algorithmsSeq, validationsSeq);
    }

    public String computeExaremeQuery() {
        List<ExaremeQueryElement> queryElements = new LinkedList<>();
        for (Variable var : model.getQuery().getVariables())
        {
            ExaremeQueryElement el = new ExaremeQueryElement();
            el.setName("variable");
            el.setDesc("");
            el.setValue(var.getCode());
            queryElements.add(el);
        }
        for (Variable var : model.getQuery().getCovariables())
        {
            ExaremeQueryElement el = new ExaremeQueryElement();
            el.setName("covariables");
            el.setDesc("");
            el.setValue(var.getCode());
            queryElements.add(el);
        }
        for (Variable var : model.getQuery().getGrouping())
        {
            ExaremeQueryElement el = new ExaremeQueryElement();
            el.setName("groupings");
            el.setDesc("");
            el.setValue(var.getCode());
            queryElements.add(el);
        }

        ExaremeQueryElement tableEl = new ExaremeQueryElement();
        tableEl.setName("showtable");
        tableEl.setDesc("");
        tableEl.setValue("TotalResults");
        queryElements.add(tableEl);

        ExaremeQueryElement formatEl = new ExaremeQueryElement();
        formatEl.setName("format");
        formatEl.setDesc("");
        formatEl.setValue("True");
        queryElements.add(formatEl);

        return gson.toJson(queryElements);
    }

    public JsonObject jsonify() {
        JsonObject exp = gson.toJsonTree(this).getAsJsonObject();
        JsonParser parser = new JsonParser();

        if (this.algorithms != null)
        {
            exp.remove("algorithms");
            JsonArray jsonAlgorithms = parser.parse(this.algorithms).getAsJsonArray();
            exp.add("algorithms", jsonAlgorithms);
        }

        if (this.validations != null)
        {
            exp.remove("validations");
            JsonArray jsonValidations = parser.parse(this.validations).getAsJsonArray();
            exp.add("validations", jsonValidations);
        }

        if (this.result != null)
        {
            exp.remove("result");
            JsonArray jsonResult = parser.parse(this.result).getAsJsonArray();
            exp.add("result", jsonResult);
        }

        return exp;
    }

    public String getValidations() {
        return validations;
    }

    public void setValidations(String validations) {
        this.validations = validations;
    }

    public String getAlgorithms() {
        return algorithms;
    }

    public void setAlgorithms(String algorithms) {
        this.algorithms = algorithms;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public boolean isHasError() {
        return hasError;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Date getFinished() {
        return finished;
    }

    public void setFinished(Date finished) {
        this.finished = finished;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public boolean isResultsViewed() {
        return resultsViewed;
    }

    public void setResultsViewed(boolean resultsViewed) {
        this.resultsViewed = resultsViewed;
    }

    public boolean isHasServerError() {
        return hasServerError;
    }

    public void setHasServerError(boolean hasServerError) {
        this.hasServerError = hasServerError;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }
}
