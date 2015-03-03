package net.nemerosa.ontrack.extension.scm.service;

import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogFile;

import java.util.List;
import java.util.function.Function;

/**
 * Generic service around the SCM
 */
public interface SCMService {

    <T extends SCMChangeLogFile> String diff(List<T> changeLogFiles, List<String> patterns, Function<T, String> diffFn);

}
