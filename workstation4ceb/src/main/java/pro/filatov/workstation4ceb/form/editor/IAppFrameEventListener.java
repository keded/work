package pro.filatov.workstation4ceb.form.editor;

import java.io.File;

/**
 * Created by yuri.filatov on 02.08.2016.
 */
public interface IAppFrameEventListener {

    void updateCurrentFile(File newFile, File oldFile);

}
