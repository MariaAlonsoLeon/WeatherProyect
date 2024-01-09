package org.ulpgc.dacd.control.commands;

import org.ulpgc.dacd.control.exceptions.DataMartConsultingException;
import org.ulpgc.dacd.view.model.Output;
import java.util.List;

public interface Command {
    Output execute(List<String> params) throws DataMartConsultingException;
}
