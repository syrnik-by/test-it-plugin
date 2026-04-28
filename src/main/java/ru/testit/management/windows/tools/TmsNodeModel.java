package ru.testit.management.windows.tools;

import com.intellij.openapi.vfs.VirtualFile;
import ru.testit.kotlin.client.models.StepModel;
import ru.testit.kotlin.client.models.WorkItemEntityTypes;

import java.util.UUID;

public class TmsNodeModel {
    private String name;
    private Long globalId;
    private Iterable<StepModel> preconditions;
    private Iterable<StepModel> steps;
    private Iterable<StepModel> postconditions;
    private WorkItemEntityTypes entityTypeName;
    private boolean isAutomated;
    private UUID id;
    private VirtualFile file;
    private Integer line;

    public TmsNodeModel(String name, Long globalId) {
        this.name = name;
        this.globalId = globalId;
    }

    public TmsNodeModel(String name, Long globalId, Iterable<StepModel> preconditions,
                        Iterable<StepModel> steps, Iterable<StepModel> postconditions,
                        WorkItemEntityTypes entityTypeName, boolean isAutomated, UUID id) {
        this.name = name;
        this.globalId = globalId;
        this.preconditions = preconditions;
        this.steps = steps;
        this.postconditions = postconditions;
        this.entityTypeName = entityTypeName;
        this.isAutomated = isAutomated;
        this.id = id;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getGlobalId() { return globalId; }
    public void setGlobalId(Long globalId) { this.globalId = globalId; }

    public Iterable<StepModel> getPreconditions() { return preconditions; }
    public void setPreconditions(Iterable<StepModel> preconditions) { this.preconditions = preconditions; }

    public Iterable<StepModel> getSteps() { return steps; }
    public void setSteps(Iterable<StepModel> steps) { this.steps = steps; }

    public Iterable<StepModel> getPostconditions() { return postconditions; }
    public void setPostconditions(Iterable<StepModel> postconditions) { this.postconditions = postconditions; }

    public WorkItemEntityTypes getEntityTypeName() { return entityTypeName; }
    public void setEntityTypeName(WorkItemEntityTypes entityTypeName) { this.entityTypeName = entityTypeName; }

    public boolean isAutomated() { return isAutomated; }
    public void setAutomated(boolean automated) { isAutomated = automated; }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public VirtualFile getFile() { return file; }
    public void setFile(VirtualFile file) { this.file = file; }

    public Integer getLine() { return line; }
    public void setLine(Integer line) { this.line = line; }
}
