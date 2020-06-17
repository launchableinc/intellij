package com.google.idea.sdkcompat.general;

import com.intellij.codeInsight.template.impl.TemplateManagerImpl;
import com.intellij.dvcs.branch.BranchType;
import com.intellij.dvcs.branch.DvcsBranchManager;
import com.intellij.dvcs.branch.DvcsBranchSettings;
import com.intellij.find.findUsages.CustomUsageSearcher;
import com.intellij.find.findUsages.FindUsagesOptions;
import com.intellij.icons.AllIcons;
import com.intellij.ide.impl.OpenProjectTask;
import com.intellij.ide.projectView.TreeStructureProvider;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.scratch.ScratchesNamedScope;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.extensions.ProjectExtensionPointName;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkAdditionalData;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl;
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.platform.PlatformProjectOpenProcessor;
import com.intellij.psi.PsiElement;
import com.intellij.ui.EditorNotifications;
import com.intellij.ui.EditorNotificationsImpl;
import com.intellij.ui.EditorTextField;
import com.intellij.usages.Usage;
import com.intellij.util.Processor;
import java.nio.file.Path;
import java.util.Collection;
import javax.annotation.Nullable;
import javax.swing.Icon;

/** Provides SDK compatibility shims for base plugin API classes, available to all IDEs. */
public final class BaseSdkCompat {
  private BaseSdkCompat() {}

  /** #api193: made public in 2020.1. */
  public static ProjectExtensionPointName<EditorNotifications.Provider<?>>
      getEditorNotificationsEp() {
    return EditorNotificationsImpl.EP_PROJECT;
  }

  /** #api193: constructor changed in 2020.1. */
  public static class DvcsBranchManagerAdapter extends DvcsBranchManager {
    protected DvcsBranchManagerAdapter(
        Project project, DvcsBranchSettings settings, BranchType[] branchTypes) {
      super(project, settings, branchTypes);
    }
  }

  /** #api193: wildcard generics added in 2020.1. */
  public abstract static class CustomUsageSearcherAdapter extends CustomUsageSearcher {
    /** #api193: wildcard generics added in 2020.1. */
    @Override
    public void processElementUsages(
        PsiElement element, Processor<? super Usage> processor, FindUsagesOptions options) {
      doProcessElementUsages(element, processor, options);
    }

    protected abstract void doProcessElementUsages(
        PsiElement element, Processor<? super Usage> processor, FindUsagesOptions options);
  }

  /** #api193: wildcard generics added in 2020.1. */
  public interface TreeStructureProviderAdapter extends TreeStructureProvider {
    @Nullable
    default Object doGetData(Collection<AbstractTreeNode<?>> selected, String dataId) {
      return null;
    }

    @Nullable
    @Override
    default Object getData(Collection<AbstractTreeNode<?>> selected, String dataId) {
      return doGetData(selected, dataId);
    }

    @Override
    default Collection<AbstractTreeNode<?>> modify(
        AbstractTreeNode<?> parent,
        Collection<AbstractTreeNode<?>> children,
        ViewSettings settings) {
      return doModify(parent, children, settings);
    }

    Collection<AbstractTreeNode<?>> doModify(
        AbstractTreeNode<?> parent,
        Collection<AbstractTreeNode<?>> children,
        ViewSettings settings);
  }

  /** #api193: changed in 2020.1 */
  public static final String SCRATCHES_SCOPE_NAME = ScratchesNamedScope.scratchesAndConsoles();

  /** #api193: 'project' param removed in 2020.1 */
  public static void setTemplateTesting(Project project, Disposable parentDisposable) {
    TemplateManagerImpl.setTemplateTesting(parentDisposable);
  }

  /** #api193: changed in 2020.1. */
  @Nullable
  public static String getActiveToolWindowId(Project project) {
    return ToolWindowManager.getInstance(project).getActiveToolWindowId();
  }

  /** Compat class for {@link AllIcons}. */
  public static final class AllIconsCompat {
    private AllIconsCompat() {}

    /** #api193: changed in 2020.1. */
    public static final Icon collapseAll = AllIcons.Actions.Collapseall;

    /** #api193: this is unavailable (and not used) in 2020.1 */
    public static final Icon disabledRun = AllIcons.Process.Stop;

    /** #api193: this is unavailable (and not used) in 2020.1 */
    public static final Icon disabledDebug = AllIcons.Process.Stop;
  }

  /** #api193: SdkConfigurationUtil changed in 2020.1. */
  public static ProjectJdkImpl createSdk(
      Collection<? extends Sdk> allSdks,
      VirtualFile homeDir,
      SdkType sdkType,
      @Nullable SdkAdditionalData additionalData,
      @Nullable String customSdkSuggestedName) {
    return SdkConfigurationUtil.createSdk(
        allSdks, homeDir, sdkType, additionalData, customSdkSuggestedName);
  }

  /** #api193: project opening requirements changed in 2020.1. */
  public static void openProject(Project project, Path projectFile) {
    ApplicationManager.getApplication()
        .executeOnPooledThread(
            () ->
                PlatformProjectOpenProcessor.openExistingProject(
                    /* file= */ projectFile,
                    /* projectDir= */ projectFile,
                    new OpenProjectTask(project)));
  }

  /** #api193: auto-disposed with UI component in 2020.1+ */
  public static void disposeEditorTextField(EditorTextField field) {}
}
