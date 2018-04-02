/*
 * Copyright 2018 Patrik Karlström.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.trixon.filebydate.fx;

import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import se.trixon.almond.util.Dict;
import se.trixon.almond.util.SystemHelper;
import se.trixon.almond.util.fx.control.DirectoryChooserPane;
import se.trixon.almond.util.fx.control.FileChooserPane;
import se.trixon.filebydate.DateSource;
import se.trixon.filebydate.NameCase;
import se.trixon.filebydate.Profile;
import se.trixon.filebydate.ProfileManager;
import se.trixon.filebydate.ui.MainFrame;

/**
 *
 * @author Patrik Karlström
 */
public class ProfilePane extends GridPane {

    private final ResourceBundle mBundleUI = SystemHelper.getBundle(MainFrame.class, "Bundle");
    private ComboBox<NameCase> mCaseBaseComboBox;
    private ComboBox<NameCase> mCaseExtComboBox;
    private ComboBox<String> mDatePatternComboBox;
    private ComboBox<DateSource> mDateSourceComboBox;
    private TextField mDescTextField;
    private FileChooserPane mDestFileChooserPane;
    private ComboBox<String> mFilePatternComboBox;
    private CheckBox mLinksCheckBox;
    private TextField mNameTextField;
    private Button mOkButton;
    private ComboBox<String> mOperationComboBox;
    private final Profile mProfile;
    private CheckBox mRecursiveCheckBox;
    private CheckBox mReplaceCheckBox;
    private DirectoryChooserPane mSourceFileChooserPane;
    private final ProfileManager mProfileManager = ProfileManager.getInstance();

    public ProfilePane(Profile profile) {
        mProfile = profile;
        createUI();

        mNameTextField.setText(mProfile.getName());
        mDescTextField.setText(mProfile.getDescription());
        mSourceFileChooserPane.setPath(mProfile.getSourceDir());
        mDestFileChooserPane.setPath(mProfile.getDestDir());
        mFilePatternComboBox.setValue(mProfile.getFilePattern());
        mDateSourceComboBox.setValue(mProfile.getDateSource());
        mDatePatternComboBox.setValue(mProfile.getDatePattern());
        mOperationComboBox.getSelectionModel().select(mProfile.getOperation());
        mLinksCheckBox.setSelected(mProfile.isFollowLinks());
        mRecursiveCheckBox.setSelected(mProfile.isRecursive());
        mReplaceCheckBox.setSelected(mProfile.isReplaceExisting());
        mCaseBaseComboBox.setValue(mProfile.getCaseBase());
        mCaseExtComboBox.setValue(mProfile.getCaseExt());

        Platform.runLater(() -> {
            initValidation();
            mNameTextField.requestFocus();
        });
    }

    public void save() {
        mProfile.setName(mNameTextField.getText().trim());
        mProfile.setDescription(mDescTextField.getText());
        mProfile.setSourceDir(mSourceFileChooserPane.getPath());
        mProfile.setDestDir(mDestFileChooserPane.getPath());
        mProfile.setFilePattern(mFilePatternComboBox.getValue());
        mProfile.setDateSource(mDateSourceComboBox.getValue());
        mProfile.setDatePattern(mDatePatternComboBox.getValue());
        mProfile.setOperation(mOperationComboBox.getSelectionModel().getSelectedIndex());
        mProfile.setFollowLinks(mLinksCheckBox.isSelected());
        mProfile.setRecursive(mRecursiveCheckBox.isSelected());
        mProfile.setReplaceExisting(mReplaceCheckBox.isSelected());
        mProfile.setCaseBase(mCaseBaseComboBox.getValue());
        mProfile.setCaseExt(mCaseExtComboBox.getValue());
    }

    private void createUI() {
        //setGridLinesVisible(true);

        Label nameLabel = new Label(Dict.NAME.toString());
        Label descLabel = new Label(Dict.DESCRIPTION.toString());
        Label sourceLabel = new Label(Dict.SOURCE.toString());
        Label destLabel = new Label(Dict.DESTINATION.toString());
        Label filePatternLabel = new Label(Dict.FILE_PATTERN.toString());
        Label dateSourceLabel = new Label(Dict.DATE_SOURCE.toString());
        Label datePatternLabel = new Label(Dict.DATE_PATTERN.toString());
        Label operationLabel = new Label(Dict.OPERATION.toString());
        Label caseBaseLabel = new Label(Dict.BASENAME.toString());
        Label caseExtLabel = new Label(Dict.EXTENSION.toString());

        mLinksCheckBox = new CheckBox(Dict.FOLLOW_LINKS.toString());
        mRecursiveCheckBox = new CheckBox(Dict.RECURSIVE.toString());
        mReplaceCheckBox = new CheckBox(Dict.REPLACE.toString());

        mCaseBaseComboBox = new ComboBox<>();
        mDatePatternComboBox = new ComboBox<>();
        mDateSourceComboBox = new ComboBox<>();
        mFilePatternComboBox = new ComboBox<>();
        mOperationComboBox = new ComboBox<>();
        mCaseExtComboBox = new ComboBox<>();

        mNameTextField = new TextField();
        mDescTextField = new TextField();

        mSourceFileChooserPane = new DirectoryChooserPane(Dict.OPEN.toString());
        mDestFileChooserPane = new FileChooserPane(Dict.OPEN.toString());

        mFilePatternComboBox.setEditable(true);
        mDatePatternComboBox.setEditable(true);

        int col = 0;
        int row = 0;

        add(nameLabel, col, row, REMAINING, 1);
        add(mNameTextField, col, ++row, REMAINING, 1);
        add(descLabel, col, ++row, REMAINING, 1);
        add(mDescTextField, col, ++row, REMAINING, 1);
        add(sourceLabel, col, ++row, REMAINING, 1);
        add(mSourceFileChooserPane, col, ++row, REMAINING, 1);
        add(destLabel, col, ++row, REMAINING, 1);
        add(mDestFileChooserPane, col, ++row, REMAINING, 1);

        GridPane patternPane = new GridPane();
        patternPane.addRow(0, filePatternLabel, dateSourceLabel, datePatternLabel);
        patternPane.addRow(1, mFilePatternComboBox, mDateSourceComboBox, mDatePatternComboBox);
        patternPane.setHgap(8);
        addRow(++row, patternPane);

        GridPane.setHgrow(mFilePatternComboBox, Priority.ALWAYS);
        GridPane.setHgrow(mDateSourceComboBox, Priority.ALWAYS);
        GridPane.setHgrow(mDatePatternComboBox, Priority.ALWAYS);

        GridPane.setFillWidth(mFilePatternComboBox, true);
        GridPane.setFillWidth(mDateSourceComboBox, true);
        GridPane.setFillWidth(mDatePatternComboBox, true);

        double width = 100.0 / 3.0;
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(width);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(width);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(width);
        patternPane.getColumnConstraints().addAll(col1, col2, col3);

        mFilePatternComboBox.setMaxWidth(Double.MAX_VALUE);
        mDateSourceComboBox.setMaxWidth(Double.MAX_VALUE);
        mDatePatternComboBox.setMaxWidth(Double.MAX_VALUE);
        GridPane subPane = new GridPane();
        //subPane.setGridLinesVisible(true);
        subPane.addRow(0, operationLabel, new Label(), new Label(), new Label(), caseBaseLabel, caseExtLabel);
        subPane.addRow(1, mOperationComboBox, mLinksCheckBox, mRecursiveCheckBox, mReplaceCheckBox, mCaseBaseComboBox, mCaseExtComboBox);
        subPane.setHgap(8);
        add(subPane, col, ++row, REMAINING, 1);

        final Insets rowInsets = new Insets(0, 0, 8, 0);

        GridPane.setMargin(mNameTextField, rowInsets);
        GridPane.setMargin(mDescTextField, rowInsets);
        GridPane.setMargin(mSourceFileChooserPane, rowInsets);
        GridPane.setMargin(mDestFileChooserPane, rowInsets);
        GridPane.setMargin(patternPane, rowInsets);

        mFilePatternComboBox.setItems(FXCollections.observableArrayList(
                "*",
                "{*.jpg,*.JPG}",
                "{*.mp4,*.MP4}"
        ));

        mDatePatternComboBox.setItems(FXCollections.observableArrayList(
                "yyyy/MM/yyyy-MM-dd",
                "yyyy/MM/yyyy-MM-dd/HH",
                "yyyy/MM/dd",
                "yyyy/ww",
                "yyyy/ww/u"
        ));

        mCaseBaseComboBox.setItems(FXCollections.observableArrayList(Arrays.asList(NameCase.values())));
        mCaseExtComboBox.setItems(FXCollections.observableArrayList(Arrays.asList(NameCase.values())));
        mDateSourceComboBox.setItems(FXCollections.observableArrayList(Arrays.asList(DateSource.values())));
        mOperationComboBox.setItems(FXCollections.observableArrayList(Arrays.asList(mBundleUI.getString("operations").split("\\|"))));
    }

    private void initValidation() {
        final String text_is_required = "Text is required";
        boolean indicateRequired = false;

        Predicate namePredicate = (Predicate) (Object o) -> {
            return mProfileManager.isValid(mProfile.getName(), (String) o);
        };

        ValidationSupport validationSupport = new ValidationSupport();
        validationSupport.registerValidator(mNameTextField, indicateRequired, Validator.createEmptyValidator(text_is_required));
        validationSupport.registerValidator(mNameTextField, indicateRequired, Validator.createPredicateValidator(namePredicate, text_is_required));
        validationSupport.registerValidator(mDescTextField, indicateRequired, Validator.createEmptyValidator(text_is_required));
        validationSupport.registerValidator(mSourceFileChooserPane.getTextField(), indicateRequired, Validator.createEmptyValidator(text_is_required));
        validationSupport.registerValidator(mDestFileChooserPane.getTextField(), indicateRequired, Validator.createEmptyValidator(text_is_required));
        validationSupport.registerValidator(mFilePatternComboBox, indicateRequired, Validator.createEmptyValidator(text_is_required));
        validationSupport.registerValidator(mDatePatternComboBox, indicateRequired, Validator.createEmptyValidator(text_is_required));

        validationSupport.validationResultProperty().addListener((ObservableValue<? extends ValidationResult> observable, ValidationResult oldValue, ValidationResult newValue) -> {
            mOkButton.setDisable(validationSupport.isInvalid());
        });

        mFilePatternComboBox.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            mFilePatternComboBox.setValue(newValue);
        });

        mDatePatternComboBox.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            mDatePatternComboBox.setValue(newValue);
        });

        validationSupport.initInitialDecoration();
    }

    void setOkButton(Button button) {
        mOkButton = button;
    }
}
