package eu.hbp.mip.model;

import ch.chuv.lren.mip.portal.WokenConversions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import ch.chuv.lren.woken.messages.datasets.DatasetId;
import ch.chuv.lren.woken.messages.query.ExperimentQuery;
import ch.chuv.lren.woken.messages.query.*;
import eu.hbp.mip.utils.TypesConvert;
import ch.chuv.lren.woken.messages.query.filters.FilterRule;
import ch.chuv.lren.woken.messages.variables.FeatureIdentifier;
import org.hibernate.annotations.Cascade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Option;
import scala.collection.JavaConversions;

import javax.persistence.*;
import java.lang.reflect.Type;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(Experiment.class);

    private static final Gson gson = new Gson();

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

    public ExperimentQuery prepareQuery(String user) {
        if (model == null || model.getQuery() == null)
            return new ExperimentQuery(null, null, null, null, null, Option.empty(), null, null, null, null, null, null);

        List<AlgorithmSpec> algorithms = new LinkedList<>();
        Type algoList = new TypeToken<LinkedList<eu.hbp.mip.model.Algorithm>>(){}.getType();
        List<eu.hbp.mip.model.Algorithm> algos = new Gson().fromJson(this.algorithms, algoList);
        for (eu.hbp.mip.model.Algorithm a: algos) {
            algorithms.add(new AlgorithmSpec(a.getCode(), TypesConvert.algoParamsToScala(a.getParameters())));
        }

        List<ValidationSpec> validations = new LinkedList<>();
        Type validList = new TypeToken<LinkedList<eu.hbp.mip.model.ExperimentValidator>>(){}.getType();
        List<eu.hbp.mip.model.ExperimentValidator> valids = new Gson().fromJson(this.validations, validList);
        for (ExperimentValidator v: valids) {
            validations.add(new ValidationSpec(v.getCode(), TypesConvert.algoParamsToScala(v.getParameters())));
        }

        scala.collection.immutable.List<FeatureIdentifier> variablesSeq =
                TypesConvert.variablesToIdentifiers(model.getQuery().getVariables());
        scala.collection.immutable.List<FeatureIdentifier> covariablesSeq =
                TypesConvert.variablesToIdentifiers(model.getQuery().getCovariables());
        scala.collection.immutable.List<FeatureIdentifier> groupingSeq =
                TypesConvert.variablesToIdentifiers(model.getQuery().getGrouping());
        scala.collection.immutable.List<AlgorithmSpec> algorithmsSeq = JavaConversions.asScalaBuffer(algorithms).toList();
        scala.collection.immutable.List<ValidationSpec> validationsSeq = JavaConversions.asScalaBuffer(validations).toList();

        WokenConversions conv = new WokenConversions();
        scala.collection.immutable.Set<DatasetId> trainingDatasets = conv.toDatasets(model.getQuery().getTrainingDatasets());
        scala.collection.immutable.Set<DatasetId> testingDatasets = conv.toDatasets(model.getQuery().getTestingDatasets());
        scala.collection.immutable.Set<DatasetId> validationDatasets = conv.toDatasets(model.getQuery().getValidationDatasets());

        String filtersJson = model.getQuery().getFilters();
        Option<FilterRule> filters = conv.toFilterRule(filtersJson);
        UserId userId = new UserId(user);

        return new ExperimentQuery(userId, variablesSeq, covariablesSeq, groupingSeq, filters, Option.empty(),
                trainingDatasets, testingDatasets, algorithmsSeq, validationDatasets,
                validationsSeq, Option.empty());
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

        if (this.result != null && !this.hasServerError)
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
