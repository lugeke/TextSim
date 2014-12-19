package VSM;

import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;


public class CalService extends Service<ObservableList<CalResult>>{
    @Override
    protected Task<ObservableList<CalResult>> createTask() {
        return new CalTask();
    }

}
