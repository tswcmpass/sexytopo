package org.hwyl.sexytopo.control.io;

import android.content.Context;

import androidx.documentfile.provider.DocumentFile;

import org.hwyl.sexytopo.SexyTopoConstants;
import org.hwyl.sexytopo.control.io.translation.AbstractSurveyFile;
import org.hwyl.sexytopo.model.survey.Survey;

import java.io.IOException;
import java.util.List;

@SuppressWarnings("UnnecessaryLocalVariable")
public class SurveyFile extends AbstractSurveyFile {

    public static class SurveyFileType {

        private final String extension;
        private final String mimeType;

        public final SurveyFileType AUTOSAVE;

        public SurveyFileType(String extension) {
            this(extension, SexyTopoConstants.DEFAULT_MIME_TYPE);
        }

        public SurveyFileType(String extension, String mimeType) {
            this.extension = extension;
            this.mimeType = mimeType;

            if (!extension.endsWith(SexyTopoConstants.AUTOSAVE_EXTENSION)) {
                AUTOSAVE = new SurveyFileType(withExtension(extension, SexyTopoConstants.AUTOSAVE_EXTENSION));
            } else {
                AUTOSAVE = null;
            }
        }

        public String getExtension() {
            return extension;
        }

        public SurveyFile get(Survey survey) {
            return new SurveyFile(survey, this);
        }

        public SurveyFile get(SurveyDirectory surveyDirectory) {
            return new SurveyFile(surveyDirectory.getSurvey(), surveyDirectory, this);
        }

    }

    public static final SurveyFileType DATA = new SurveyFileType(SexyTopoConstants.DATA_EXTENSION);
    public static final SurveyFileType METADATA =
        new SurveyFileType(SexyTopoConstants.METADATA_EXTENSION);
    public static final SurveyFileType SKETCH_PLAN =
        new SurveyFileType(SexyTopoConstants.PLAN_SKETCH_EXTENSION);
    public static final SurveyFileType SKETCH_EXT_ELEVATION =
        new SurveyFileType(SexyTopoConstants.EXT_ELEVATION_SKETCH_EXTENSION);

    public static final List<SurveyFileType> ALL_TYPES =
            List.of(DATA, METADATA, SKETCH_PLAN, SKETCH_EXT_ELEVATION);
    private final SurveyFileType surveyFileType;

    private SurveyFile(Survey survey, SurveyFileType surveyFileType) {
        this(survey, SurveyDirectory.TOP.get(survey), surveyFileType);
    }

    private SurveyFile(Survey survey, SurveyDirectory parent, SurveyFileType surveyFileType) {
        this.survey = survey;
        this.parent = parent;
        this.surveyFileType = surveyFileType;
    }

    public SurveyFile getAutosaveVersion() {
        SurveyFileType autosaveType = surveyFileType.AUTOSAVE;
        if (autosaveType == null) {
            return this;
        } else {
            return autosaveType.get(survey);
        }
    }

    public String getFilename() {
        String filename = withExtension(survey.getName(), surveyFileType.extension);
        return filename;
    }

    public static String withExtension(String filename, String extension) {
        return filename + "." + extension;
    }

    public DocumentFile getDocumentFile(Context context) {
        DocumentFile surveyDirectory = parent.getDocumentFile(context);
        String filename = getFilename();
        DocumentFile documentFile = surveyDirectory.findFile(filename);
        return documentFile;
    }

    public DocumentFile createDocumentFile(Context context) {
        DocumentFile directoryDocumentFile = parent.getDocumentFile(context);
        String filename = getFilename();
        DocumentFile documentFile =
                directoryDocumentFile.createFile(surveyFileType.mimeType, filename);
        return documentFile;
    }

    public DocumentFile getOrCreateDocumentFile(Context context) {
        DocumentFile documentFile = getDocumentFile(context);
        if (documentFile == null) {
            documentFile = createDocumentFile(context);
        }
        return documentFile;
    }



    public void save(Context context, String contents) throws IOException {
        parent.ensureExists(context);
        DocumentFile documentFile = getOrCreateDocumentFile(context);
        IoUtils.saveToFile(context, documentFile, contents);
    }

    public String slurp(Context context)
            throws IOException {
        DocumentFile file = getDocumentFile(context);
        String content = IoUtils.slurpFile(context, file);
        return content;
    }
}
