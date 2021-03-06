package com.nusinfineon.ui;

import static com.nusinfineon.util.Directories.ICON_APPLICATION;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.nusinfineon.core.Core;
import com.nusinfineon.exceptions.CustomException;
import com.nusinfineon.storage.JsonParser;
import com.nusinfineon.util.LotSequencingRule;
import com.nusinfineon.util.Messages;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * Represents the front-end model and logic of the whole user interface window.
 */
public class MainGui extends UiPart<Stage> {
    private static final int MAX_ALLOWABLE_BATCH_SIZE = 24;
    private static final int MIN_ALLOWABLE_BATCH_SIZE = 1;
    private static final int MAX_ALLOWABLE_STEP_SIZE = MAX_ALLOWABLE_BATCH_SIZE - MIN_ALLOWABLE_BATCH_SIZE;
    private static final int MIN_ALLOWABLE_STEP_SIZE = 1;
    private static final String FXML = "MainGui.fxml";

    private Stage primaryStage;
    private Core core;
    private JsonParser jsonParser;

    @FXML
    private HBox exeDragTarget;
    @FXML
    private TextField flexsimExeLocation;
    @FXML
    private HBox modelDragTarget;
    @FXML
    private TextField modelFileLocation;
    @FXML
    private HBox inputFileDragTarget;
    @FXML
    private TextField inputFileLocation;
    @FXML
    private HBox outputFileDragTarget;
    @FXML
    private TextField outputFileLocation;
    @FXML
    private TextField runSpeed;
    @FXML
    private TextField stopTime;
    @FXML
    private Spinner<Integer> batchSizeMin;
    @FXML
    private Spinner<Integer> batchSizeMax;
    @FXML
    private Spinner<Integer> batchSizeStep;
    @FXML
    private CheckBox lotSequencingRuleFCFS;
    @FXML
    private CheckBox lotSequencingRuleSPT;
    @FXML
    private CheckBox lotSequencingRuleMJ;
    @FXML
    private CheckBox lotSequencingRuleRAND;
    @FXML
    private RadioButton resourceSelectCriteria1;
    @FXML
    private RadioButton resourceSelectCriteria2;
    @FXML
    private RadioButton resourceSelectCriteria3;
    @FXML
    private RadioButton resourceSelectCriteria4;
    @FXML
    private RadioButton lotSelectionCriteria1;
    @FXML
    private RadioButton lotSelectionCriteria2;
    @FXML
    private RadioButton lotSelectionCriteria3;
    @FXML
    private RadioButton trolleyLocationSelectCriteria0;
    @FXML
    private RadioButton trolleyLocationSelectCriteria1;
    @FXML
    private RadioButton trolleyLocationSelectCriteria2;
    @FXML
    private RadioButton bibLoadOnLotCriteria1;
    @FXML
    private RadioButton bibLoadOnLotCriteria2;
    @FXML
    private CheckBox showModel;
    @FXML
    private CheckBox openTableauServer;

    private ToggleGroup resourceSelectCriteria;
    private ToggleGroup lotSelectionCriteria;
    private ToggleGroup trolleyLocationSelectCriteria;
    private ToggleGroup bibLoadOnLotCriteria;

    /**
     * Constructor for MainGui object.
     * @param primaryStage
     * @param core
     * @param jsonParser
     */
    public MainGui(Stage primaryStage, Core core, JsonParser jsonParser) {
        super(FXML, primaryStage);

        // Set dependencies
        this.primaryStage = primaryStage;
        this.core = core;
        this.jsonParser = jsonParser;

        configureUi();
    }

    /**
     * Configures the UI from data in Core.
     */
    private void configureUi() {
        flexsimExeLocation.setText(core.getFlexsimLocation());
        modelFileLocation.setText(core.getModelLocation());
        inputFileLocation.setText(core.getInputLocation());
        outputFileLocation.setText(core.getOutputLocation());
        runSpeed.setText(core.getRunSpeed());
        stopTime.setText(core.getStopTime());
        showModel.setSelected(core.getIsModelShown());
        openTableauServer.setSelected(core.getOpenTableauServer());

        lotSequencingRuleFCFS.setSelected(getIsFCFSSelected(core.getLotSequencingRules()));
        lotSequencingRuleSPT.setSelected(getIsSPTSelected(core.getLotSequencingRules()));
        lotSequencingRuleMJ.setSelected(getIsMJSelected(core.getLotSequencingRules()));
        lotSequencingRuleRAND.setSelected(getIsRANDSelected(core.getLotSequencingRules()));

        batchSizeMin.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                MIN_ALLOWABLE_BATCH_SIZE, MAX_ALLOWABLE_BATCH_SIZE, Integer.parseInt(core.getBatchSizeMinString())));
        batchSizeMax.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                MIN_ALLOWABLE_BATCH_SIZE, MAX_ALLOWABLE_BATCH_SIZE, Integer.parseInt(core.getBatchSizeMaxString())));
        batchSizeStep.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                MIN_ALLOWABLE_STEP_SIZE, MAX_ALLOWABLE_STEP_SIZE, Integer.parseInt(core.getBatchSizeStepString())));

        resourceSelectCriteria = new ToggleGroup();
        resourceSelectCriteria1.setToggleGroup(resourceSelectCriteria);
        resourceSelectCriteria2.setToggleGroup(resourceSelectCriteria);
        resourceSelectCriteria3.setToggleGroup(resourceSelectCriteria);
        resourceSelectCriteria4.setToggleGroup(resourceSelectCriteria);
        resourceSelectCriteria.selectToggle(getResourceSelectCriteria());

        lotSelectionCriteria = new ToggleGroup();
        lotSelectionCriteria1.setToggleGroup(lotSelectionCriteria);
        lotSelectionCriteria2.setToggleGroup(lotSelectionCriteria);
        lotSelectionCriteria3.setToggleGroup(lotSelectionCriteria);
        lotSelectionCriteria.selectToggle(getLotSelectionCriteria());

        trolleyLocationSelectCriteria = new ToggleGroup();
        trolleyLocationSelectCriteria0.setToggleGroup(trolleyLocationSelectCriteria);
        trolleyLocationSelectCriteria1.setToggleGroup(trolleyLocationSelectCriteria);
        trolleyLocationSelectCriteria2.setToggleGroup(trolleyLocationSelectCriteria);
        trolleyLocationSelectCriteria.selectToggle(getTrolleyLocationSelectCriteria());

        bibLoadOnLotCriteria = new ToggleGroup();
        bibLoadOnLotCriteria1.setToggleGroup(bibLoadOnLotCriteria);
        bibLoadOnLotCriteria2.setToggleGroup(bibLoadOnLotCriteria);
        bibLoadOnLotCriteria.selectToggle(getBibLoadOnLotCriteria());
    }

    /**
     * Checks if FCFS is selected.
     * @param lotSequencingRules List of lot sequencing rules
     * @return true or false
     */
    private Boolean getIsFCFSSelected(HashMap<LotSequencingRule, Boolean> lotSequencingRules) {
        return lotSequencingRules.get(LotSequencingRule.FCFS);
    }

    /**
     * Checks if SPT is selected.
     * @param lotSequencingRules List of lot sequencing rules
     * @return true or false
     */
    private Boolean getIsSPTSelected(HashMap<LotSequencingRule, Boolean> lotSequencingRules) {
        return lotSequencingRules.get(LotSequencingRule.SPT);
    }

    /**
     * Checks if MJ is selected.
     * @param lotSequencingRules List of lot sequencing rules
     * @return true or false
     */
    private Boolean getIsMJSelected(HashMap<LotSequencingRule, Boolean> lotSequencingRules) {
        return lotSequencingRules.get(LotSequencingRule.MJ);
    }

    /**
     * Checks if RAND is selected.
     * @param lotSequencingRules List of lot sequencing rules
     * @return true or false
     */
    private Boolean getIsRANDSelected(HashMap<LotSequencingRule, Boolean> lotSequencingRules) {
        return lotSequencingRules.get(LotSequencingRule.RAND);
    }

    /**
     * Gets the saved radio button for Resource Select Criteria.
     * @return Saved selection
     */
    private RadioButton getResourceSelectCriteria() {
        String selection = core.getResourceSelectCriteria();
        switch (selection) {
        case "1":
            return resourceSelectCriteria1;
        case "2":
            return resourceSelectCriteria2;
        case "3":
            return resourceSelectCriteria3;
        default:
            return resourceSelectCriteria4;
        }
    }

    /**
     * Gets the saved radio button for Lot Selection Criteria.
     * @return Saved selection
     */
    private RadioButton getLotSelectionCriteria() {
        String selection = core.getLotSelectionCriteria();
        switch (selection) {
        case "1":
            return lotSelectionCriteria1;
        case "2":
            return lotSelectionCriteria2;
        default:
            return lotSelectionCriteria3;
        }
    }

    /**
     * Gets the saved radio button for Trolley Location Select Criteria.
     * @return Saved selection
     */
    private RadioButton getTrolleyLocationSelectCriteria() {
        String selection = core.getTrolleyLocationSelectCriteria();
        switch (selection) {
        case "0":
            return trolleyLocationSelectCriteria0;
        case "1":
            return trolleyLocationSelectCriteria1;
        default:
            return trolleyLocationSelectCriteria2;
        }
    }

    /**
     * Gets the saved radio button for BIB Load on Lot Criteria.
     * @return Saved selection
     */
    private RadioButton getBibLoadOnLotCriteria() {
        String selection = core.getBibLoadOnLotCriteria();
        switch (selection) {
        case "1":
            return bibLoadOnLotCriteria1;
        default:
            return bibLoadOnLotCriteria2;
        }
    }

    /**
     * For drag and drop functionality of base FlexSim model.
     * @param event
     */
    @FXML
    public void modelDragOver(DragEvent event) {
        if (event.getGestureSource() != modelDragTarget
                && event.getDragboard().hasFiles()) {
            /* allow for both copying and moving, whatever user chooses */
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }

    /**
     * For drag and drop functionality of base FlexSim model.
     * @param event
     */
    @FXML
    public void modelDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        final boolean isAccepted = db.getFiles().get(0).getName().toLowerCase().endsWith(".fsm");

        if (db.hasFiles() && isAccepted ) {
            modelFileLocation.setText(db.getFiles().toString().replaceAll("\\[", "").replaceAll("\\]",""));
            success = true;
        } else {
            showInvalidBox("File must be a FlexSim model with extension .fsm");
        }
        /* let the source know whether the string was successfully transferred and used */
        event.setDropCompleted(success);
        event.consume();
    }

    /**
     * For drag and drop functionality of input Excel file.
     * @param event
     */
    @FXML
    public void inputDragOver(DragEvent event) {
        if (event.getGestureSource() != inputFileDragTarget
                && event.getDragboard().hasFiles()) {
            /* allow for both copying and moving, whatever user chooses */
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }

    /**
     * For drag and drop functionality of input Excel file.
     * @param event
     */
    @FXML
    public void inputDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        final boolean isAccepted = db.getFiles().get(0).getName().toLowerCase().endsWith(".xlsx");

        if (db.hasFiles() && isAccepted ) {
            inputFileLocation.setText(db.getFiles().toString().replaceAll("\\[", "").replaceAll("\\]",""));
            success = true;
        } else {
            showInvalidBox("File must be an Excel file with extension .xlsx");
        }
        /* let the source know whether the string was successfully transferred and used */
        event.setDropCompleted(success);
        event.consume();
    }

    /**
     * For drag and drop functionality of output Excel file.
     * @param event
     */
    @FXML
    public void outputDragOver(DragEvent event) {
        if (event.getGestureSource() != outputFileDragTarget
                && event.getDragboard().hasFiles()) {
            /* allow for both copying and moving, whatever user chooses */
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }

    /**
     * For drag and drop functionality of output Excel file.
     * @param event
     */
    @FXML
    public void outputDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        final boolean isAccepted = db.getFiles().get(0).getName().toLowerCase().endsWith(".xlsx");

        if (db.hasFiles() && isAccepted ) {
            outputFileLocation.setText(db.getFiles().toString().replaceAll("\\[", "").replaceAll("\\]",""));
            success = true;
        } else {
            showInvalidBox("File must be an Excel file with extension .xlsx");
        }
        /* let the source know whether the string was successfully transferred and used */
        event.setDropCompleted(success);
        event.consume();
    }

    /**
     * For drag and drop functionality of flexsim.exe.
     * @param event
     */
    @FXML
    public void exeDragOver(DragEvent event) {
        if (event.getGestureSource() != exeDragTarget
                && event.getDragboard().hasFiles()) {
            /* allow for both copying and moving, whatever user chooses */
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }

    /**
     * For drag and drop functionality of flexsim.exe.
     * @param event
     */
    @FXML
    public void exeDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        final boolean isAccepted = db.getFiles().get(0).getName().toLowerCase().endsWith(".exe");

        if (db.hasFiles() && isAccepted) {
            flexsimExeLocation.setText(db.getFiles().toString().replaceAll("\\[", "").replaceAll("\\]",""));
            success = true;
        } else {
            showInvalidBox("File must be a executable file with extension .exe");
        }
        /* let the source know whether the string was successfully transferred and used */
        event.setDropCompleted(success);
        event.consume();
    }

    /**
     * Show About box.
     */
    @FXML
    private void handleAbout() {
        String title = "About IBIS Simulation";
        String text = Messages.ABOUT_MESSAGE;

        Alert aboutAlert = raiseAlertBox(Alert.AlertType.INFORMATION, title, null, text, 480, 300);
        aboutAlert.showAndWait();
    }

    /**
     * Resets input fields to default.
     */
    @FXML
    private void handleDefault() {
        core.inputData(null, null, null, null, null, null, false, false, null, null, null, null, null, null, null, null);
        configureUi();
    }

    /**
     * Saves the current input fields.
     * @throws IOException
     */
    @FXML
    private void handleSave() throws IOException {
        if (confirmSave()) {
            saveInputDataToCore();
            jsonParser.storeData(core);
        }
    }

    /**
     * Loads the saved input fields.
     * @throws IOException
     */
    @FXML
    private void handleLoad() throws IOException {
        if (confirmLoad()) {
            try {
                core = jsonParser.loadData();
                configureUi();
                saveInputDataToCore();
            } catch (UnrecognizedPropertyException | JsonParseException e) {
                showErrorBox(Messages.SAVE_FILE_WRONG_FORMAT_MESSAGE);
            } catch (FileNotFoundException e) {
                showErrorBox(Messages.SAVE_FILE_NOT_FOUND_MESSAGE);
            }
        }
    }

    /**
     * Closes the application.
     * @throws IOException
     */
    @FXML
    private void handleExit() throws IOException {
        saveInputDataToCore();
        jsonParser.storeData(core);
        primaryStage.hide();
    }

    /**
     * Handles the main execution of the simulation.
     */
    @FXML
    public void handleModelExecution() {
        if (inputValidated()) {
            if (confirmRun(batchSizeMin.getValueFactory().getValue(), batchSizeMax.getValueFactory().getValue(),
                    batchSizeStep.getValueFactory().getValue(), getLotSequencingRules())) {
                Alert waitAlert = getWaitAlert();
                waitAlert.show();
                try {
                    execute();
                } catch (IOException e) {
                    showErrorBox("An IO Exception has occurred. Please try again.\n" + e.getMessage());
                } catch (CustomException e) {
                    showErrorBox("An error has occurred. Please try again.\n" + e.getMessage());
                }
                Stage stage = (Stage) waitAlert.getDialogPane().getScene().getWindow();
                stage.close();
            }
        }
    }

    /**
     * Saves input data to Core.
     */
    private void saveInputDataToCore() {
        core.inputData(flexsimExeLocation.getText(), modelFileLocation.getText(), inputFileLocation.getText(),
                outputFileLocation.getText(), runSpeed.getText(), stopTime.getText(), showModel.isSelected(),
                openTableauServer.isSelected(),
                getLotSequencingRules(),
                Integer.toString(batchSizeMin.getValueFactory().getValue()),
                Integer.toString(batchSizeMax.getValueFactory().getValue()),
                Integer.toString(batchSizeStep.getValueFactory().getValue()),
                getSelectedResourceSelectCriteria(resourceSelectCriteria),
                getSelectedLotSelectionCriteria(lotSelectionCriteria),
                getSelectedTrolleyLocationSelectCriteria(trolleyLocationSelectCriteria),
                getSelectedBibLoadOnLotCriteria(bibLoadOnLotCriteria));
    }

    /**
     * Checks if all input fields are valid.
     * Invalid fields will append the respective error message and return False.
     * @return true or false
     */
    private boolean inputValidated() {
        String errorMessage = "";
        boolean execute = true;

        if (!isLotSequenceSelected()) {
            errorMessage = errorMessage + "At least 1 Lot Sequencing Rule must be selected!\n";
            execute = false;
        }
        if (!isValidMinBatchSize(batchSizeMin.getValueFactory().getValue())) {
            errorMessage = errorMessage + "Lowest batch size to run must be at least 1 and at most 24!\n";
            execute = false;
        }
        if (!isValidMaxBatchSize(batchSizeMax.getValueFactory().getValue())) {
            errorMessage = errorMessage + "Highest batch size to run must be at least 1 and at most 24!\n";
            execute = false;
        }
        if (!isValidMinMax(batchSizeMin.getValueFactory().getValue(),
                batchSizeMax.getValueFactory().getValue())) {
            errorMessage = errorMessage + "Lowest batch size (" + batchSizeMin.getValueFactory().getValue() +
                    ") cannot be larger than highest batch size (" + batchSizeMax.getValueFactory().getValue() + ")!\n";
            execute = false;
        } else if (!isValidStepSize(batchSizeStep.getValueFactory().getValue(),
                batchSizeMin.getValueFactory().getValue(),
                batchSizeMax.getValueFactory().getValue())) {
            errorMessage = errorMessage + "Step Size between Runs (BIB Batch Size) cannot exceed " +
                    Math.max(1, (batchSizeMax.getValueFactory().getValue() - batchSizeMin.getValueFactory().getValue()))
                    + "!\n";
            execute = false;
        }
        if (isBlankRunParams()) {
            errorMessage = errorMessage + "Run Parameters cannot be blank!\n";
            execute = false;
        } else if (isNotDouble(runSpeed.getText()) || isNotDouble(stopTime.getText())) {
            errorMessage = errorMessage + "Run Parameters must be numeric (integer/double)!\n";
            execute = false;
        } else if (!isValidRunParams(runSpeed.getText(), stopTime.getText())) {
            errorMessage = errorMessage + "Run Speed must be smaller than Stop Time!\n";
            execute = false;
        }
        if (flexsimExeLocationIsBlank()) {
            errorMessage = errorMessage + "FlexSim (.exe) directory is blank!\n";
            execute = false;
        } else if (!isFoundFiles(flexsimExeLocation.getText())) {
            errorMessage = errorMessage + "FlexSim (.exe) address cannot be found!\n";
            execute = false;
        } else if (!isValidExeLocation()) {
            errorMessage = errorMessage + "FlexSim (.exe) must be the executable file: flexsim.exe\n";
            execute = false;
        }
        if (modelFileLocationIsBlank()) {
            errorMessage = errorMessage + "Model (.fsm) directory is blank!\n";
            execute = false;
        } else if (!isFoundFiles(modelFileLocation.getText())) {
            errorMessage = errorMessage + "Model (.fsm) address cannot be found!\n";
            execute = false;
        } else if (!isValidExtension(modelFileLocation.getText(), "fsm")) {
            errorMessage = errorMessage + "Model (.fsm) must be a FlexSim model with extension .fsm!\n";
            execute = false;
        }
        if (inputFileLocationIsBlank()) {
            errorMessage = errorMessage + "Input (.xlsx) directory is blank!\n";
            execute = false;
        } else if (!isFoundFiles(inputFileLocation.getText())) {
            errorMessage = errorMessage + "Input (.xlsx) address cannot be found!\n";
            execute = false;
        } else if (!isValidExtension(inputFileLocation.getText(), "xlsx")) {
            errorMessage = errorMessage + "Input (.xlsx) must be an Excel file with extension .xlsx!\n";
            execute = false;
        }
        if (outputFileLocationIsBlank()) {
            errorMessage = errorMessage + "Output (.xlsx) directory is blank!\n";
            execute = false;
        } else if (!isFoundFiles(outputFileLocation.getText())) {
            errorMessage = errorMessage + "Output (.xlsx) address cannot be found!\n";
            execute = false;
        } else if (!isValidExtension(outputFileLocation.getText(), "xlsx")) {
            errorMessage = errorMessage + "Output (.xlsx) must be an Excel file with extension .xlsx!\n";
            execute = false;
        }

        if (!execute) {
            showInvalidBox(errorMessage);
        }
        return execute;
    }

    /**
     * Executes Core with confirmation, waiting and completion alerts.
     * @throws IOException
     * @throws CustomException
     */
    private void execute() throws IOException, CustomException {
        saveInputDataToCore();
        jsonParser.storeData(core);
        core.execute();
        showCompletedBox();
    }

    /**
     * Helper function to raise alert box with a supplied alert text for Wait.
     * @return Wait alert box
     */
    private Alert getWaitAlert() {
        String title = "Simulation running... (Please wait...)";
        String header = "Please wait for the simulation to complete...";
        String text = "Please wait for the simulation to complete...";
        Alert waitAlert = raiseAlertBox(Alert.AlertType.NONE, title, header, text, 480, 60);

        return waitAlert;
    }

    /**
     * Get the selected Lot Sequencing Rules from user input.
     * @return Selected Lot Sequencing Rules
     */
    private HashMap<LotSequencingRule, Boolean> getLotSequencingRules() {
        HashMap<LotSequencingRule, Boolean> lotSequencingRules = new HashMap<>();

        lotSequencingRules.put(LotSequencingRule.FCFS, lotSequencingRuleFCFS.isSelected());
        lotSequencingRules.put(LotSequencingRule.SPT, lotSequencingRuleSPT.isSelected());
        lotSequencingRules.put(LotSequencingRule.MJ, lotSequencingRuleMJ.isSelected());
        lotSequencingRules.put(LotSequencingRule.RAND, lotSequencingRuleRAND.isSelected());

        return lotSequencingRules;
    }

    /**
     * Get the selected radio button for Resource Select Criteria from user input and convert to number character.
     * @param resourceSelectCriteria Toggle group for Resource Select Criteria
     * @return Selected Resource Select Criteria
     */
    private String getSelectedResourceSelectCriteria(ToggleGroup resourceSelectCriteria) {
        Toggle selection = resourceSelectCriteria.getSelectedToggle();
        if (selection == resourceSelectCriteria1) {
            return "1";
        } else if (selection == resourceSelectCriteria2) {
            return "2";
        } else if (selection == resourceSelectCriteria3) {
            return "3";
        } else {
            return "4";
        }
    }

    /** Get the selected radio button for Lot Selection Criteria from user input and convert to number character.
     * @param lotSelectionCriteria Toggle group for Lot Selection Criteria
     * @return Selected Lot Selection Criteria
     */
    private String getSelectedLotSelectionCriteria(ToggleGroup lotSelectionCriteria) {
        Toggle selection = lotSelectionCriteria.getSelectedToggle();
        if (selection == lotSelectionCriteria1) {
            return "1";
        } else if (selection == lotSelectionCriteria2) {
            return "2";
        } else {
            return "3";
        }
    }

    /** Get the selected radio button for Trolley Location Select Criteria from user input and convert to number character.
     * @param trolleyLocationSelectCriteria Toggle group for Trolley Location Select Criteria
     * @return Selected Trolley Location Select Criteria
     */
    private String getSelectedTrolleyLocationSelectCriteria(ToggleGroup trolleyLocationSelectCriteria) {
        Toggle selection = trolleyLocationSelectCriteria.getSelectedToggle();
        if (selection == trolleyLocationSelectCriteria0) {
            return "0";
        } else if (selection == trolleyLocationSelectCriteria1) {
            return "1";
        } else {
            return "2";
        }
    }

    /** Get the selected radio button for BIB Load On Lot Criteria from user input and convert to number character.
     * @param bibLoadOnLotCriteria Toggle group for BIB Load On Lot Criteria
     * @return Selected BIB Load On Lot Criteria
     */
    private String getSelectedBibLoadOnLotCriteria(ToggleGroup bibLoadOnLotCriteria) {
        Toggle selection = bibLoadOnLotCriteria.getSelectedToggle();
        if (selection == bibLoadOnLotCriteria1) {
            return "1";
        } else {
            return "2";
        }
    }

    /**
     * Helper function to raise dialog box for save confirmation.
     * @return true when user clicks OK to confirm
     */
    private boolean confirmSave() {
        String title = "Save Input";
        String header = "Confirm to save input data?";
        String text = Messages.CONFIRM_SAVE_MESSAGE;
        Alert confirmationAlert = raiseAlertBox(Alert.AlertType.CONFIRMATION, title, header, text);

        confirmationAlert.showAndWait();
        if (confirmationAlert.getResult() == ButtonType.OK) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Helper function to raise dialog box for load confirmation.
     * @return true when user clicks OK to confirm
     */
    private boolean confirmLoad() {
        String title = "Open Input";
        String header = "Confirm to load saved input data?";
        String text = Messages.CONFIRM_LOAD_MESSAGE;
        Alert confirmationAlert = raiseAlertBox(Alert.AlertType.CONFIRMATION, title, header, text);

        confirmationAlert.showAndWait();
        if (confirmationAlert.getResult() == ButtonType.OK) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Helper function to raise alert box with a supplied alert text for Confirmation.
     * @return true when user clicks OK to confirm
     */
    private boolean confirmRun(int batchSizeMin, int batchSizeMax, int batchSizeStep,
                               HashMap<LotSequencingRule, Boolean> lotSequencingRules) {
        int numberOfRuns = 0;
        for (Map.Entry<LotSequencingRule, Boolean> rule : lotSequencingRules.entrySet()) {
            if (rule.getValue()) {
                numberOfRuns++;
            }
        }
        numberOfRuns = numberOfRuns*((batchSizeMax - batchSizeMin) / batchSizeStep + 1);

        String title = "Confirm Simulation";
        String header = "Confirm to run simulation?";
        String text = "There will be " + numberOfRuns + " simulation run(s).\n\n"
                + Messages.CONFIRM_RUN_MESSAGE;
        Alert confirmationAlert = raiseAlertBox(Alert.AlertType.CONFIRMATION, title, header, text);

        confirmationAlert.showAndWait();
        if (confirmationAlert.getResult() == ButtonType.OK) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Helper function to raise alert box with a supplied alert text for invalid input.
     * @param alertText Alert text string to be displayed to the user.
     */
    private void showInvalidBox(String alertText) {
        String title = "Invalid Input";
        String header = "Invalid Input";
        Alert errorAlert = raiseAlertBox(Alert.AlertType.ERROR, title, header, alertText);

        errorAlert.showAndWait();
    }

    /**
     * Helper function to raise alert box with a supplied alert text for Errors.
     * @param alertText Alert text string to be displayed to the user.
     */
    private void showErrorBox(String alertText) {
        String title = "Error";
        Alert errorAlert = raiseAlertBox(Alert.AlertType.ERROR, title, null, alertText);

        errorAlert.showAndWait();
    }

    /**
     * Helper function to raise alert box with a supplied alert text for Completion.
     */
    private void showCompletedBox() {
        String title = "Simulation Complete";
        String header = "Simulation has completed!";
        String text = Messages.COMPLETED_MESSAGE;
        Alert completeAlert = raiseAlertBox(Alert.AlertType.INFORMATION, title, header, text);

        completeAlert.showAndWait();
    }

    /**
     * Returns true if FlexSim .exe location is blank.
     * @return true or false
     */
    private boolean flexsimExeLocationIsBlank() {
        if (flexsimExeLocation.getText() == null || flexsimExeLocation.getText().isBlank()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns true if Model file location is blank.
     * @return true or false
     */
    private boolean modelFileLocationIsBlank() {
        if (modelFileLocation.getText() == null || modelFileLocation.getText().isBlank()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns true if Input file location is blank.
     * @return true or false
     */
    private boolean inputFileLocationIsBlank() {
        if (inputFileLocation.getText() == null || inputFileLocation.getText().isBlank()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns true if Output file location is blank.
     * @return true or false
     */
    private boolean outputFileLocationIsBlank() {
        if (outputFileLocation.getText() == null || outputFileLocation.getText().isBlank()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns true if the given file locations can be found.
     * @return true or false
     */
    private boolean isFoundFiles(String fileLocation) {
        File file = new File(fileLocation);

        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns true if flexsim.exe is valid.
     * @return true or false
     */
    private boolean isValidExeLocation() {
        String fileName = flexsimExeLocation.getText().substring(flexsimExeLocation.getText().lastIndexOf("\\") + 1);

        if (fileName.equals("flexsim.exe")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns true if file extension is valid.
     * @return true or false
     */
    private boolean isValidExtension(String fileLocation, String extension) {
        String fileName = fileLocation.substring(fileLocation.lastIndexOf(".") + 1);

        if (fileName.equals(extension)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns true if any of the required simulation run parameters are blank.
     * @return true or false
     */
    private boolean isBlankRunParams() {
        if (runSpeed.getText() == null || runSpeed.getText().isBlank() ||
                stopTime.getText() == null || stopTime.getText().isBlank()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns true if at least one lot sequencing rule is selected.
     * @return true or false
     */
    private boolean isLotSequenceSelected() {
        HashMap<LotSequencingRule, Boolean> lotSequencingRules = getLotSequencingRules();

        for (Map.Entry<LotSequencingRule, Boolean> rule : lotSequencingRules.entrySet()) {
            if (rule.getValue() == true) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if min is lesser than max.
     * @param minBatchSize Min batch size
     * @param maxBatchSize Max batch size
     * @return true or false
     */
    private boolean isValidMinMax(int minBatchSize, int maxBatchSize) {
        if (minBatchSize <= maxBatchSize) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns true if the batch step size is acceptable i.e. smaller than difference between min and max batch sizes.
     * @param stepSize Step size
     * @param minBatchSize Min batch size
     * @param maxBatchSize Max batch size
     * @return true or false
     */
    private boolean isValidStepSize(int stepSize, int minBatchSize, int maxBatchSize) {
        try {
            if ((maxBatchSize == minBatchSize) && (stepSize == 1)){
                return true;
            } else if (stepSize <= (maxBatchSize - minBatchSize)) {
                return true;
            } else {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Returns true if a batchMinString falls within the range of
     * MIN_ALLOWABLE_BATCH_SIZE and MAX_ALLOWABLE_BATCH_SIZE.
     * @param minBatchSize Min batch size
     * @return true or false
     */
    private boolean isValidMinBatchSize(int minBatchSize) {
        try {
            if ( (minBatchSize >= MIN_ALLOWABLE_BATCH_SIZE) && (minBatchSize <= MAX_ALLOWABLE_BATCH_SIZE)) {
                return true;
            } else {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Follows the same logic as "isValidMinBatchSize".
     * @param maxBatchSize Max batch size
     * @return true or false
     */
    private boolean isValidMaxBatchSize(int maxBatchSize){
        return isValidMinBatchSize(maxBatchSize);
    }

    /**
     * Returns true if the argument string can be converted to a Double primitive data type.
     * @param str String argument
     * @return true or false
     */
    private boolean isNotDouble(String str) {
        try {
            Double.parseDouble(str);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    /**
     * Returns true if the argument string can be converted to a Double primitive data type.
     * @param runSpeed Run Speed string
     * @param stopTime Stop Time string
     * @return true or false
     */
    private boolean isValidRunParams(String runSpeed, String stopTime) {
        if (Double.parseDouble(runSpeed) <= Double.parseDouble(stopTime)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Generic function to raise alert box.
     * @param type Type of alert box
     * @param title Title message of alert box window
     * @param header Header message
     * @param text Main text message
     * @param prefWidth Preferred width of alert box window
     * @param prefHeight Preferred height of alert box window
     * @return Alert box
     */
    private Alert raiseAlertBox(Alert.AlertType type, String title, String header, String text,
                                int prefWidth, int prefHeight) {
        Alert alert = new Alert(type);

        // Text
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(text);

        // Properties
        alert.setResizable(true);
        alert.getDialogPane().setPrefSize(prefWidth, prefHeight);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(this.getClass().getResource(ICON_APPLICATION).toString()));

        return alert;
    }

    /**
     * Generic function to raise alert box with default dimensions.
     * @param type Type of alert box
     * @param title Title message of alert box window
     * @param header Header message
     * @param text Main text message
     * @return Alert box
     */
    private Alert raiseAlertBox(Alert.AlertType type, String title, String header, String text) {
        return raiseAlertBox(type, title, header, text, 480, 240);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    void show() {
        primaryStage.show();
    }
}
