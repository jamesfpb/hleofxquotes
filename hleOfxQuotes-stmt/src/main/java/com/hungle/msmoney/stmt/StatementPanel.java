package com.hungle.msmoney.stmt;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ProgressMonitor;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.Keymap;

import org.apache.log4j.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.hungle.msmoney.core.gui.StripedTableRenderer;
import com.hungle.msmoney.core.misc.CheckNullUtils;
import com.hungle.msmoney.core.misc.Utils;
import com.hungle.msmoney.core.ofx.ImportUtils;
import com.hungle.msmoney.core.ofx.StreamConsumer;
import com.hungle.msmoney.stmt.fi.AbstractFiDir;
import com.hungle.msmoney.stmt.fi.DefaultFiDir;
import com.hungle.msmoney.stmt.fi.props.FIBean;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.ObservableElementList;
import ca.odell.glazedlists.ObservableElementList.Connector;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.TransformedList;
import ca.odell.glazedlists.gui.AbstractTableComparatorChooser;
import ca.odell.glazedlists.gui.AdvancedTableFormat;
import ca.odell.glazedlists.impl.beans.BeanTableFormat;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import ca.odell.glazedlists.swing.GlazedListsSwing;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;

// TODO: Auto-generated Javadoc
/**
 * The Class StatementPanel.
 */
public class StatementPanel extends JPanel {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The Constant LOGGER. */
    private static final Logger LOGGER = Logger.getLogger(StatementPanel.class);

    /** The Constant CURRENT_CERTIFICATES_TXT. */
    static final String CURRENT_CERTIFICATES_TXT = "currentCertificates.txt";

    /** The Constant SAVED_CERTIFICATES_TXT. */
    static final String SAVED_CERTIFICATES_TXT = "savedCertificates.txt";

    /** The fi beans. */
    private EventList<FiBean> fiBeans = new BasicEventList<FiBean>();

    /** The fi beans selection model. */
    DefaultEventSelectionModel<FiBean> fiBeansSelectionModel;
    
    /** The thread pool. */
    private final Executor threadPool = Executors.newCachedThreadPool();
    
    /** The command view. */
    private JPanel commandView;

    /** The fi properties text area. */
    JTextArea fiPropertiesTextArea;
    
    /** The fi properties file. */
    File fiPropertiesFile;

    /** The fi request text area. */
    private JTextArea fiRequestTextArea;

    /** The fi response text area. */
    private JTextArea fiResponseTextArea;
    
    /** The response file. */
    private File responseFile;

    /** The fi error text area. */
    private JTextArea fiErrorTextArea;

    /** The details for bean. */
    FiBean detailsForBean;

    /** The saved certificates text area. */
    private JTextArea savedCertificatesTextArea;
    
    /** The saved certificates file. */
    private File savedCertificatesFile;

    /** The current certificates text area. */
    private JTextArea currentCertificatesTextArea;
    
    /** The current certificates file. */
    private File currentCertificatesFile;

    /** The certificate status. */
    private JLabel certificateStatus;

    /** The save certificates button. */
    private JButton saveCertificatesButton;

    /** The tabbed pane. */
    private JTabbedPane tabbedPane;

    /** The show diffs button. */
    private JButton showDiffsButton;

    /** The fi dir. */
    private File fiDir = new File(FIBean.DEFAULT_FI_DIR);

    private boolean showProgress = false;

    /**
     * Instantiates a new statement panel.
     */
    public StatementPanel() {
        super();
        setLayout(new BorderLayout());
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setLeftComponent(createFiListView());
        splitPane.setRightComponent(createFiDetailView());
        splitPane.setDividerLocation(0.50);
        splitPane.setResizeWeight(0.50);

        add(splitPane, BorderLayout.CENTER);
    }

    /**
     * Creates the fi detail view.
     *
     * @return the component
     */
    private Component createFiDetailView() {
        // JPanel view = new JPanel();
        // view.setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Properties", createFiPropertiesView());
        tabbedPane.addTab("OFX Request", createFiRequestView());
        tabbedPane.addTab("OFX Response", createFiResponseView());
        tabbedPane.addTab("Download Error", createFiErrorView());
        tabbedPane.addTab("SSL Certificates", createSSLCertificatesView());

        // view.add(tabbedPane, BorderLayout.CENTER);

        return tabbedPane;
    }

    /**
     * Creates the fi error view.
     *
     * @return the component
     */
    private Component createFiErrorView() {
        JPanel view = new JPanel();
        view.setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane();
        view.add(scrollPane, BorderLayout.CENTER);

        fiErrorTextArea = new JTextArea();
        fiErrorTextArea.setEditable(false);
        scrollPane.setViewportView(fiErrorTextArea);

        return view;
    }

    /**
     * Creates the SSL certificates view.
     *
     * @return the component
     */
    private Component createSSLCertificatesView() {
        JPanel view = new JPanel();
        view.setLayout(new BorderLayout());

        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

        certificateStatus = new JLabel("Status: NO_STATUS");
        statusPanel.add(certificateStatus, BorderLayout.WEST);

        JPanel xxx = new JPanel();
        xxx.setLayout(new BoxLayout(xxx, BoxLayout.LINE_AXIS));
        statusPanel.add(xxx, BorderLayout.EAST);

        saveCertificatesButton = new JButton(new AbstractAction("Save Certificates") {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent event) {
                LOGGER.info("> Save Certificates: " + detailsForBean.getFiUrl());
                if (detailsForBean == null) {
                    LOGGER.warn("detailsForBean is null.");
                    return;
                }

                File respFile = detailsForBean.getUpdater().getRespFile();
                File savedCertificates = new File(respFile.getAbsoluteFile().getParentFile(), SAVED_CERTIFICATES_TXT);
                File currentCertificates = new File(respFile.getAbsoluteFile().getParentFile(),
                        CURRENT_CERTIFICATES_TXT);
                try {
                    Utils.copyFile(currentCertificates, savedCertificates);
                    JOptionPane.showMessageDialog(StatementPanel.this,
                            "Saved current SSL certificates succesfully.\nPlease try download again.",
                            "Saving SSL Certificates", JOptionPane.PLAIN_MESSAGE);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(StatementPanel.this, e.toString(), "Error saving SSL Certificates",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        saveCertificatesButton.setEnabled(false);
        xxx.add(saveCertificatesButton);

        xxx.add(Box.createHorizontalStrut(5));
        showDiffsButton = new JButton(new ShowDiffTask(this, "Show Diffs"));
        showDiffsButton.setEnabled(false);
        xxx.add(showDiffsButton);

        view.add(statusPanel, BorderLayout.NORTH);

        JPopupMenu popupMenu = null;
        JMenuItem menuItem = null;
        PopupListener popupListener = null;
        AbstractAction action = null;

        JScrollPane leftComponent = new JScrollPane();
        leftComponent.setBorder(BorderFactory.createTitledBorder("Saved SSL Certificates"));
        savedCertificatesTextArea = new JTextArea();
        savedCertificatesTextArea.setEditable(false);
        popupMenu = new JPopupMenu();
        menuItem = null;
        action = new AbstractAction("Copy full path of this file") {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                StringSelection stringSelection = new StringSelection(savedCertificatesFile.getAbsolutePath());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                ClipboardOwner owner = new ClipboardOwner() {
                    @Override
                    public void lostOwnership(Clipboard clipboard, Transferable contents) {
                    }
                };
                clipboard.setContents(stringSelection, owner);
            }
        };
        menuItem = new JMenuItem(action);
        popupMenu.add(menuItem);
        popupListener = new PopupListener(popupMenu);
        savedCertificatesTextArea.addMouseListener(popupListener);
        leftComponent.setViewportView(savedCertificatesTextArea);

        JScrollPane rightComponent = new JScrollPane();
        rightComponent.setBorder(BorderFactory.createTitledBorder("Current SSL Certificates"));
        currentCertificatesTextArea = new JTextArea();
        currentCertificatesTextArea.setEditable(false);
        popupMenu = new JPopupMenu();
        menuItem = null;
        action = new AbstractAction("Copy full path of this file") {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentCertificatesFile != null) {
                    StringSelection stringSelection = new StringSelection(currentCertificatesFile.getAbsolutePath());
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    ClipboardOwner owner = new ClipboardOwner() {
                        @Override
                        public void lostOwnership(Clipboard clipboard, Transferable contents) {
                        }
                    };
                    clipboard.setContents(stringSelection, owner);
                }
            }
        };
        menuItem = new JMenuItem(action);
        popupMenu.add(menuItem);
        popupListener = new PopupListener(popupMenu);
        currentCertificatesTextArea.addMouseListener(popupListener);
        rightComponent.setViewportView(currentCertificatesTextArea);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftComponent, rightComponent);
        splitPane.setDividerLocation(0.50);
        splitPane.setResizeWeight(0.50);

        view.add(splitPane, BorderLayout.CENTER);

        return view;
    }

    /**
     * Creates the fi response view.
     *
     * @return the component
     */
    private Component createFiResponseView() {
        JPanel view = new JPanel();
        view.setLayout(new BorderLayout());

        // JScrollPane scrollPane = new JScrollPane();
        // view.add(scrollPane, BorderLayout.CENTER);
        //
        // fiResponseTextArea = new JTextArea();
        // fiResponseTextArea.setEditable(false);
        // scrollPane.setViewportView(fiResponseTextArea);

        RSyntaxTextArea textArea = new RSyntaxTextArea();
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
        RTextScrollPane sp = new RTextScrollPane(textArea);
        fiResponseTextArea = textArea;
        fiResponseTextArea.setEditable(false);

        view.add(sp, BorderLayout.CENTER);

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem menuItem = null;
        menuItem = new JMenuItem(new AbstractAction("Format") {

            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent event) {
                LOGGER.info("> format responseFile=" + responseFile);

                File formatter = new File("tools/OFXFormatter/OFX Formatter.exe");
                if (!formatter.exists()) {
                    JOptionPane.showMessageDialog(StatementPanel.this, "No formatter");
                    return;
                }
                formatter = formatter.getAbsoluteFile();
                LOGGER.info("Has formmatter=" + formatter.getAbsolutePath());

                try {
                    List<String> command = new ArrayList<String>();
                    command.add(formatter.getAbsolutePath());
                    command.add(responseFile.getAbsolutePath());

                    ProcessBuilder builder = new ProcessBuilder(command);

                    final Process process = builder.start();
                    final InputStream stdout = process.getInputStream();
                    threadPool.execute(new StreamConsumer(stdout, "stdout"));
                    final InputStream stderr = process.getErrorStream();
                    threadPool.execute(new StreamConsumer(stderr, "stderr"));

                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("pre proc.waitFor()");
                    }
                    int status = process.waitFor();
                    if (status != 0) {
                        LOGGER.warn("Formatter command failed with exit status=" + status);
                        LOGGER.warn("  command=" + command);
                    } else {
                        LOGGER.info("Formatter command OK with exit status=" + status);

                        File file = new File(responseFile.getParentFile(), "resp.tab");
                        // setText(fiResponseTextArea, file);
                        Reader in = null;
                        try {
                            in = new BufferedReader(new FileReader(file));
                            fiResponseTextArea.read(in, "resp.tab");
                        } catch (IOException e) {
                            LOGGER.error(e);
                        } finally {
                            if (in != null) {
                                try {
                                    in.close();
                                } catch (IOException e) {
                                    LOGGER.warn(e);
                                } finally {
                                    in = null;
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    LOGGER.error(e);
                } catch (InterruptedException e) {
                    LOGGER.error(e);
                }
            }
        });
        popupMenu.add(menuItem);
        fiResponseTextArea.addMouseListener(new PopupListener(popupMenu));

        return view;
    }

    /**
     * Creates the fi properties view.
     *
     * @return the component
     */
    private Component createFiPropertiesView() {
        JPanel view = new JPanel();
        view.setLayout(new BorderLayout());

        // JScrollPane scrollPane = new JScrollPane();
        // fiPropertiesTextArea = new JTextArea();
        // scrollPane.setViewportView(fiPropertiesTextArea);
        // view.add(scrollPane, BorderLayout.CENTER);

        RSyntaxTextArea textArea = new RSyntaxTextArea();
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PROPERTIES_FILE);
        RTextScrollPane sp = new RTextScrollPane(textArea);
        fiPropertiesTextArea = textArea;
        view.add(sp, BorderLayout.CENTER);

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem menuItem = null;
        SavePropertiesAction saveAction = new SavePropertiesAction(this, "Save");
        menuItem = new JMenuItem(saveAction);
        popupMenu.add(menuItem);

        popupMenu.addSeparator();

        menuItem = new JMenuItem(new AbstractAction("Cut") {

            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                fiPropertiesTextArea.cut();
            }
        });
        popupMenu.add(menuItem);
        menuItem = new JMenuItem(new AbstractAction("Copy") {

            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                fiPropertiesTextArea.copy();
            }
        });
        popupMenu.add(menuItem);
        menuItem = new JMenuItem(new AbstractAction("Paste") {

            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                fiPropertiesTextArea.paste();
            }
        });
        popupMenu.add(menuItem);

        PopupListener popupListener = new PopupListener(popupMenu);
        fiPropertiesTextArea.addMouseListener(popupListener);

        Keymap keyMap = fiPropertiesTextArea.getKeymap();
        // control-s to save
        KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK);
        keyMap.addActionForKeyStroke(keyStroke, saveAction);

        return view;
    }

    /**
     * Creates the fi request view.
     *
     * @return the component
     */
    private Component createFiRequestView() {
        JPanel view = new JPanel();
        view.setLayout(new BorderLayout());

        // JScrollPane scrollPane = new JScrollPane();
        // view.add(scrollPane, BorderLayout.CENTER);
        //
        // fiRequestTextArea = new JTextArea();
        // fiRequestTextArea.setEditable(false);
        // scrollPane.setViewportView(fiRequestTextArea);

        RSyntaxTextArea textArea = new RSyntaxTextArea();
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
        RTextScrollPane sp = new RTextScrollPane(textArea);
        fiRequestTextArea = textArea;
        fiRequestTextArea.setEditable(false);

        view.add(sp, BorderLayout.CENTER);

        return view;
    }

    /**
     * Creates the fi list view.
     *
     * @return the component
     */
    private Component createFiListView() {
        JPanel view = new JPanel();
        view.setLayout(new BorderLayout());

        JTabbedPane tabbed = new JTabbedPane();
        tabbed.add("Financial Institutions", createFiListView1());
        view.add(tabbed, BorderLayout.CENTER);

        return view;
    }

    /**
     * Creates the fi list view 1.
     *
     * @return the component
     */
    private Component createFiListView1() {
        JPanel view = new JPanel();
        view.setLayout(new BorderLayout());

        JTextField filterEdit = new JTextField(10);
        JScrollPane scrollPane = createScrolledFiTable(filterEdit);
        view.add(scrollPane, BorderLayout.CENTER);

        commandView = new JPanel();
        commandView.setLayout(new BoxLayout(commandView, BoxLayout.LINE_AXIS));
        commandView.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

        JButton button = null;
        button = new JButton(new AbstractAction("Refresh") {

            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                refreshFiDir();
            }
        });
        commandView.add(button);

        commandView.add(Box.createHorizontalStrut(6));

        JButton downloadSelectedButton = new JButton(new AbstractAction("Download") {

            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                downloadStatements(fiBeansSelectionModel.getSelected());
            }
        });
        commandView.add(downloadSelectedButton);

        commandView.add(Box.createHorizontalStrut(3));

        JButton importSelectedButton = new JButton(new AbstractAction("Import") {

            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                importFis(fiBeansSelectionModel.getSelected());
            }
        });
        commandView.add(importSelectedButton);

        commandView.add(Box.createHorizontalStrut(6));

        JButton downloadAllButton = new JButton(new AbstractAction("Download All") {

            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                downloadStatements(fiBeans);
            }
        });
        commandView.add(downloadAllButton);
        commandView.add(Box.createHorizontalStrut(3));
        JButton importAllButton = new JButton(new AbstractAction("Import All") {

            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                importFis(fiBeans);
            }
        });
        commandView.add(importAllButton);

        // commandView.add(Box.createHorizontalGlue());

        view.add(commandView, BorderLayout.SOUTH);

        return view;
    }

    /**
     * Creates the scrolled fi table.
     *
     * @param filterEdit the filter edit
     * @return the j scroll pane
     */
    private JScrollPane createScrolledFiTable(JTextField filterEdit) {
        Comparator<? super FiBean> comparator = new Comparator<FiBean>() {

            @Override
            public int compare(FiBean o1, FiBean o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };
        // JTextField filterEdit = new JTextField(10);
        TextFilterator<FiBean> filter = null;
        // String propertyNames[] = { "stockName", "stockSymbol", };
        // filter = new BeanTextFilterator(propertyNames);
        filter = new TextFilterator<FiBean>() {

            @Override
            public void getFilterStrings(List<String> list, FiBean bean) {
                list.add(bean.getName());
            }
        };
        boolean addStripe = true;
        JTable table = createFiStatusTable(fiBeans, comparator, filterEdit, filter, addStripe);
        // table.setFillsViewportHeight(true);
        JScrollPane scrolledPane = new JScrollPane(table);

        return scrolledPane;
    }

    /**
     * Refresh fi dir.
     */
    public void refreshFiDir() {
        final List<AbstractFiDir> updaters = loadFiDir();

        Runnable doRun = new Runnable() {

            @Override
            public void run() {
                try {
                    fiBeans.getReadWriteLock().writeLock().lock();
                    fiBeans.clear();
                    for (AbstractFiDir updater : updaters) {
                        FiBean bean = new FiBean();
                        try {
                            refreshBean(bean, updater);
                            fiBeans.add(bean);
                        } catch (IOException e) {
                            LOGGER.error(e);
                            bean = null;
                        }
                    }
                } finally {
                    fiBeans.getReadWriteLock().writeLock().unlock();
                }
            }
        };
        SwingUtilities.invokeLater(doRun);
    }

    /**
     * Refresh bean.
     *
     * @param bean the bean
     * @param fiDir the updater
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected void refreshBean(FiBean bean, AbstractFiDir fiDir) throws IOException {
        bean.setUpdater(fiDir);
        
        bean.setName(fiDir.getDir().getName());
        
        FIBean fi = fiDir.getFiBean();
        if (fi != null) {
            bean.setFi(fi);
        
            String name = fi.getName();
            if (!CheckNullUtils.isEmpty(name)) {
                bean.setName(name);
            }
        }
    }

    /**
     * Download statements.
     *
     * @param fiBeans the fi beans
     */
    protected void downloadStatements(final EventList<FiBean> fiBeans) {
        ProgressMonitor progressMonitor = null;
        if (showProgress) {
            progressMonitor = new ProgressMonitor(StatementPanel.this,
                    "Downloading ...                                           \t", "", 0, fiBeans.size());
        }
        
        updateStarted(fiBeans, progressMonitor);
        
        Runnable command = new DownloadStatementsTask(fiBeans, progressMonitor) {

            @Override
            protected void updateCompleted() {
              StatementPanel.this.updateCompleted(fiBeans, getProgressMonitor());
            }
            
        };
        threadPool.execute(command);
    }

    /**
     * Load fi dir.
     *
     * @return the list
     */
    private List<AbstractFiDir> loadFiDir() {
        List<AbstractFiDir> updaters = new ArrayList<AbstractFiDir>();

        // TODO_FI
        // File topDir = new File("fi");
        File topDir = getFiDir();
        LOGGER.info("> Loading fiDir=" + topDir);

        if (!topDir.isDirectory()) {
            LOGGER.warn("Not a directory topDir=" + topDir);
            return updaters;
        }

        // OfxPostClient.initVelocity();

        FileFilter filter = new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                if (pathname == null) {
                    return false;
                }
                String name = pathname.getName();
                if (name.startsWith(".")) {
                    return false;
                }
                if (name.startsWith("-")) {
                    return false;
                }

                if (pathname.isDirectory()) {
                    return true;
                }

                return false;
            }
        };
        File[] dirs = topDir.listFiles(filter);
        for (File dir : dirs) {
            try {
                LOGGER.info("Loading dir=" + dir.getAbsolutePath());
                AbstractFiDir updater = new DefaultFiDir(dir);
                updaters.add(updater);
            } catch (IOException e) {
                LOGGER.error(e);
            }
        }

        return updaters;
    }

    /**
     * Update started.
     *
     * @param fiBeans the fi beans
     * @param progressMonitor the progress monitor
     */
    private void updateStarted(final EventList<FiBean> fiBeans, final ProgressMonitor progressMonitor) {
        LOGGER.info("> updateStarted");

        Runnable doRun = new Runnable() {
            @Override
            public void run() {
                setRecursiveEnabled(commandView, false);

                if (progressMonitor != null) {
                    progressMonitor.setProgress(0);
                }
            }
        };
        SwingUtilities.invokeLater(doRun);
    }

    /**
     * Update completed.
     *
     * @param fiBeans the fi beans
     * @param progressMonitor the progress monitor
     */
    void updateCompleted(final EventList<FiBean> fiBeans, final ProgressMonitor progressMonitor) {
        LOGGER.info("> updateCompleted");
        Runnable doRun = new Runnable() {

            @Override
            public void run() {
                if (progressMonitor != null) {
                    progressMonitor.setProgress(fiBeans.size() + 10);
                }
                setRecursiveEnabled(commandView, true);
 
                EventList<FiBean> selected = fiBeansSelectionModel.getSelected();
                
                if ((selected == null) || (selected.size() == 0)) {
                    updateDetailPane(null);
                }
                
                FiBean lastSelected = null;
                for (FiBean select : selected) {
                    lastSelected = select;
                }
                if (lastSelected != null) {
                    updateDetailPane(lastSelected);
                }
                // for(FiBean bean: fiBeans) {
                // updateDetailPane(bean);
                // }
            }
        };
        SwingUtilities.invokeLater(doRun);
    }

    /**
     * Sets the recursive enabled.
     *
     * @param root the root
     * @param enable the enable
     */
    private static final void setRecursiveEnabled(Container root, boolean enable) {
        Component children[] = root.getComponents();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(children.length);
        }
        for (int i = 0; i < children.length; i++) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(children[i].getClass().getName());
            }
            if (children[i] instanceof Container) {
                setRecursiveEnabled((Container) children[i], enable);
                children[i].setEnabled(enable);
            } else {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(children[i] + ", " + enable);
                }
                children[i].setEnabled(enable);
            }
        }
    }

    /**
     * Import fis.
     *
     * @param fiBeans the fi beans
     */
    protected void importFis(final EventList<FiBean> fiBeans) {
        final ProgressMonitor progressMonitor = null;
        updateStarted(fiBeans, progressMonitor);
        Runnable command = new Runnable() {

            @Override
            public void run() {
                try {
                    for (FiBean fiBean : fiBeans) {
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("> START importing fi=" + fiBean.getName());
                        }
                        try {
                            String status = fiBean.getStatus();
                            if ((status != null) && (status.compareToIgnoreCase("SUCCESS") == 0)) {
                                File respFile = fiBean.getUpdater().getRespFile();
                                if (respFile == null) {
                                    respFile = new File(fiBean.getUpdater().getDir(),
                                            fiBean.getUpdater().getRespFileName());
                                }
                                if ((respFile != null) && (respFile.exists())) {
                                    List<File> files = new ArrayList<File>();
                                    files.add(respFile);
                                    try {
                                        int count = ImportUtils.doImport(threadPool, files);
                                        if (count != files.size()) {
                                            LOGGER.warn("Last import failed");
                                        } else {
                                            fiBean.setLastImported(new Date());
                                        }
                                    } catch (IOException e) {
                                        LOGGER.error(e, e);
                                    }
                                }
                            }
                        } finally {
                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.debug("< DONE importing fi=" + fiBean.getName());
                            }
                            long millis = 1 * 1000L;
                            try {
                                Thread.sleep(millis);
                            } catch (InterruptedException e) {
                                LOGGER.warn(e);
                            }
                        }
                    }
                } finally {
                    updateCompleted(fiBeans, progressMonitor);
                }
            }
        };
        threadPool.execute(command);
    }

    /**
     * Creates the fi status table.
     *
     * @param fiBeans the fi beans
     * @param comparator the comparator
     * @param filterEdit the filter edit
     * @param filter the filter
     * @param addStripe the add stripe
     * @return the j table
     */
    private JTable createFiStatusTable(EventList<FiBean> fiBeans, Comparator<? super FiBean> comparator,
            JTextField filterEdit, TextFilterator<FiBean> filter, boolean addStripe) {
        EventList<FiBean> source = fiBeans;

        SortedList<FiBean> sortedList = null;
        if (comparator != null) {
            sortedList = new SortedList<FiBean>(source, comparator);
            source = sortedList;
        }

        if ((filterEdit != null) && (filter != null)) {
            FilterList<FiBean> filterList = null;
            MatcherEditor<FiBean> textMatcherEditor = new TextComponentMatcherEditor<FiBean>(filterEdit, filter);
            filterList = new FilterList<FiBean>(source, textMatcherEditor);
            source = filterList;
        }

        Connector<FiBean> elementConnector = GlazedLists.beanConnector(FiBean.class);
        source = new ObservableElementList<FiBean>(source, elementConnector);
        boolean useThreadSafeList = false;
        if (useThreadSafeList) {
            source = GlazedLists.threadSafeList(source);
        }
        Class<FiBean> beanClass = FiBean.class;
        String propertyNames[] = { "name", "status", "lastDownloaded", "lastImported" };
        String columnLabels[] = { "Name", "Status", "Last Downloaded", "Last Imported" };
        AdvancedTableFormat tableFormat = new BeanTableFormat(beanClass, propertyNames, columnLabels);
        final TransformedList<FiBean, FiBean> sourceProxyList = GlazedListsSwing.swingThreadProxyList(source);
        final DefaultEventTableModel<FiBean> tableModel = new DefaultEventTableModel<FiBean>(sourceProxyList,
                tableFormat);
        JTable table = new JTable(tableModel);

        if (sortedList != null) {
            TableComparatorChooser tableSorter = TableComparatorChooser.install(table, sortedList,
                    AbstractTableComparatorChooser.SINGLE_COLUMN);
        }

        fiBeansSelectionModel = new DefaultEventSelectionModel(source);
        ListSelectionListener listener = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e == null) {
                    return;
                }
                boolean isAdjusting = e.getValueIsAdjusting();
                if (isAdjusting) {
                    return;
                }
                EventList<FiBean> beans = fiBeansSelectionModel.getSelected();
                for (FiBean bean : beans) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("selected bean=" + bean.getName());
                    }
                    updateDetailPane(bean);
                    detailsForBean = bean;
                }
            }
        };
        fiBeansSelectionModel.addListSelectionListener(listener);
        table.setSelectionModel(fiBeansSelectionModel);

        if (addStripe) {
            addStripe(tableModel, table);
        }

        final JPopupMenu popupMenu = new JPopupMenu();
        // final JPopupMenu popupMenu = null;
        if (popupMenu != null) {
            JMenuItem menuItem = null;
            
            menuItem = new JMenuItem(new GetAccountsInfoAction(this, "Get Accounts Info"));
            popupMenu.add(menuItem);

            MouseListener popupListener = new PopupListener(popupMenu);
            table.addMouseListener(popupListener);
        }

        return table;
    }

    /**
     * Adds the stripe.
     *
     * @param tableModel the table model
     * @param table the table
     */
    private static final void addStripe(final DefaultEventTableModel<FiBean> tableModel, JTable table) {
        int cols = table.getColumnModel().getColumnCount();
        for (int i = 0; i < cols; i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            TableCellRenderer renderer = new StripedTableRenderer() {

                /**
                 * 
                 */
                private static final long serialVersionUID = 1L;

                @Override
                public void setCellHorizontalAlignment(int column) {
                    super.setCellHorizontalAlignment(column);
                    if ((column == 0) || (column == 1) || (column == 2)) {
                        setHorizontalAlignment(SwingConstants.RIGHT);
                    }
                }

                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                        boolean hasFocus, int row, int column) {
                    Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                            row, column);
                    FiBean bean = tableModel.getElementAt(row);
                    if (column == 2) {
                        if (hasNewDownload(bean)) {
                            setFont(getFont().deriveFont(Font.BOLD));
                        }
                    } else if (column == 1) {
                        Exception downloadException = bean.getException();
                        String stringValue = null;
                        if (value instanceof String) {
                            stringValue = (String) value;
                        }
                        if (downloadException != null) {
                            component.setForeground(Color.RED);
                        } else {
                            if ((stringValue != null) && (stringValue.contains("ERROR"))) {
                                component.setForeground(Color.RED);
                            } else {
                                component.setForeground(null);
                            }
                        }

                        if ((stringValue != null) && (stringValue.contains("SKIP"))) {
                            setFont(getFont().deriveFont(Font.ITALIC));
                        }
                    }
                    return component;
                }

                private boolean hasNewDownload(FiBean bean) {
                    Date downloadDate = bean.getLastDownloaded();
                    Date importedDate = bean.getLastImported();
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(downloadDate + ", " + importedDate);
                    }
                    if ((downloadDate == null) && (importedDate == null)) {
                        return false;
                    }

                    if (downloadDate == null) {
                        return false;
                    }

                    if (importedDate == null) {
                        return true;
                    }

                    return downloadDate.compareTo(importedDate) > 0;
                }

            };
            column.setCellRenderer(renderer);
        }
    }

    /**
     * Update detail pane.
     *
     * @param bean the bean
     */
    protected void updateDetailPane(FiBean bean) {
        if (bean != null) {
            AbstractFiDir updater = bean.getUpdater();
            if (updater == null) {
                return;
            }
            File dir = updater.getDir();
            LOGGER.info("dir=" + dir);
        }
        
        updateFiPropertiesView(bean);
        
        updateFiRequestView(bean);
        
        updateFiResponseView(bean);
        
        updateFiErrorView(bean);
        
        updateCertificatesView(bean);
    }

    /**
     * Update fi error view.
     *
     * @param bean the bean
     */
    private void updateFiErrorView(final FiBean bean) {
        final JTextArea textArea = fiErrorTextArea;

        Runnable doRun = new Runnable() {
            @Override
            public void run() {
                textArea.setText("");
                if (bean == null) {
                    return;
                }
                AbstractFiDir updater = bean.getUpdater();
                if (updater == null) {
                    return;
                }

                // File dir = updater.getDir();
                // File file = new File(dir, "resp.ofx");
                // setText(fiResponseTextArea, file);
                Exception exception = bean.getException();
                if (exception == null) {
                    textArea.setText("No detail error");
                } else {
                    textArea.setText("");
                    StackTraceElement[] stacks = exception.getStackTrace();
                    String errorMessage = exception.toString();
                    if ((errorMessage != null) && (errorMessage.contains("peer not authenticated"))) {
                        textArea.append("Perhaps the server SSL certificates have changed?");
                        textArea.append("\n");
                        textArea.append("Check the 'SSL Certificates' tab.");
                        textArea.append("\n");
                        textArea.append("Or set property 'http.acceptAnySslCertificate=true'.");
                        textArea.append("\n");
                        textArea.append("\n");
                    }
                    textArea.append(errorMessage);
                    textArea.append("\n");
                    if (stacks != null) {
                        for (StackTraceElement stack : stacks) {
                            textArea.append("\n");
                            textArea.append(stack.toString());
                        }
                    }
                    if (tabbedPane != null) {
                        tabbedPane.setSelectedIndex(3);
                    }
                }
                textArea.setCaretPosition(0);
            }
        };
        SwingUtilities.invokeLater(doRun);
    }

    /**
     * Update fi response view.
     *
     * @param bean the bean
     */
    private void updateFiResponseView(final FiBean bean) {
        final JTextArea textArea = fiResponseTextArea;
        final String fileName = "resp.ofx";
        Runnable doRun = new Runnable() {
            @Override
            public void run() {
                File file = updateToFile(textArea, bean, fileName);
                responseFile = file;
            }
        };
        SwingUtilities.invokeLater(doRun);
    }

    /**
     * Update fi request view.
     *
     * @param bean the bean
     */
    private void updateFiRequestView(final FiBean bean) {
        final JTextArea textArea = fiRequestTextArea;
        final String fileName = "req.ofx";
        Runnable doRun = new Runnable() {
            @Override
            public void run() {
                updateToFile(textArea, bean, fileName);
            }
        };
        SwingUtilities.invokeLater(doRun);
    }

    /**
     * Update fi properties view.
     *
     * @param bean the bean
     */
    private void updateFiPropertiesView(final FiBean bean) {
        final JTextArea textArea = fiPropertiesTextArea;
        final String fileName = AbstractFiDir.DEFAULT_PROPERTIES_FILENAME;
        Runnable doRun = new Runnable() {
            @Override
            public void run() {
                boolean checkDownloaded = false;
                File file = updateToFile(fileName, textArea, bean, checkDownloaded);
                fiPropertiesFile = file;
            }
        };
        SwingUtilities.invokeLater(doRun);
    }

    /**
     * Update certificates view.
     *
     * @param bean the bean
     */
    private void updateCertificatesView(final FiBean bean) {
        final JTextArea textArea1 = savedCertificatesTextArea;
        final String fileName1 = SAVED_CERTIFICATES_TXT;

        final JTextArea textArea2 = currentCertificatesTextArea;
        final String fileName2 = CURRENT_CERTIFICATES_TXT;

        Runnable doRun = new Runnable() {
            @Override
            public void run() {
                savedCertificatesFile = updateToFile(textArea1, bean, fileName1);

                currentCertificatesFile = updateToFile(textArea2, bean, fileName2);

                if ((bean != null) && (bean.isDownloaded())) {
                    if ((savedCertificatesFile != null) && (currentCertificatesFile != null)) {
                        try {
                            if (Utils.compareFiles(savedCertificatesFile, currentCertificatesFile)) {
                                certificateStatus.setText("Status: " + "MATCHED");
                                saveCertificatesButton.setEnabled(false);
                                showDiffsButton.setEnabled(false);
                            } else {
                                certificateStatus.setText("Status: " + "NOT MATCHED");
                                saveCertificatesButton.setEnabled(true);
                                showDiffsButton.setEnabled(true);
                            }
                        } catch (IOException e) {
                            LOGGER.warn(e);
                        }
                    }
                }
            }
        };
        SwingUtilities.invokeLater(doRun);

    }

    /**
     * Update to file.
     *
     * @param textArea the text area
     * @param bean the bean
     * @param fileName the file name
     * @return the file
     */
    private File updateToFile(JTextArea textArea, FiBean bean, String fileName) {
        return updateToFile(fileName, textArea, bean, true);
    }

    /**
     * Update to file.
     * @param fileName the file name
     * @param textArea the text area
     * @param bean the bean
     * @param checkDownloaded the check downloaded
     *
     * @return the file
     */
    private File updateToFile(String fileName, JTextArea textArea, final FiBean bean, boolean checkDownloaded) {
        textArea.setText("");

        if (bean == null) {
            return null;
        }
        AbstractFiDir updater = bean.getUpdater();
        if (updater == null) {
            return null;
        }

        File dir = updater.getDir();
        File file = new File(dir, fileName);
        if (!file.exists()) {
            LOGGER.warn("No such file=" + fileName);
            return null;
        }

        file = file.getAbsoluteFile();
        if (checkDownloaded) {
            if (!bean.isDownloaded()) {
                return file;
            }
        }

        // setText(fiResponseTextArea, file);
        String desc = fileName;
        setFileContentToTextArea(file, desc, textArea);

        textArea.setCaretPosition(0);

        return file;
    }

    private void setFileContentToTextArea(File file, String desc, JTextArea textArea) {
        Reader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            textArea.read(reader, desc);
        } catch (IOException e) {
            LOGGER.error(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.warn(e);
                } finally {
                    reader = null;
                }
            }
        }
    }

    /**
     * Sets the text.
     *
     * @param textArea the text area
     * @param file the file
     */
    protected void setText(JTextArea textArea, File file) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.info("setText to file=" + file);
        }
        textArea.setText("");
        if (!file.exists()) {
            textArea.setText("No such file=" + file.getAbsolutePath());
            return;
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(line);
                }
                textArea.append(line);
                textArea.append("\n");
            }
        } catch (IOException e) {
            LOGGER.error(e);
            textArea.setText(e.toString());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.warn(e);
                } finally {
                    reader = null;
                }
            }
        }
        textArea.setCaretPosition(0);
    }

    /**
     * Gets the fi dir.
     *
     * @return the fi dir
     */
    public File getFiDir() {
        return fiDir;
    }

    /**
     * Sets the fi dir.
     *
     * @param fiDir the new fi dir
     */
    public void setFiDir(File fiDir) {
        this.fiDir = fiDir;
        LOGGER.info("fi.dir=" + this.fiDir);
    }

    public boolean isShowProgress() {
        return showProgress;
    }

    public void setShowProgress(boolean showProgress) {
        this.showProgress = showProgress;
    }

}
