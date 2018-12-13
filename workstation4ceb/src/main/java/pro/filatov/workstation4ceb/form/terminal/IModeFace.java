package pro.filatov.workstation4ceb.form.terminal;

/**
 * Created by yuri.filatov on 09.09.2016.
 */
public interface IModeFace {

    byte [] createRequest();
    void refreshDataOnFace();

}
