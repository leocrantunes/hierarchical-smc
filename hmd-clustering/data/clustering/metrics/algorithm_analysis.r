# clean memory
rm(list = ls())

# install required packages if not already installed
required_packages <- c("tidyverse", "readr", "ggplot2", "viridis", "gridExtra", "scales", "RColorBrewer")
for (pkg in required_packages) {
  if (!require(pkg, character.only = TRUE)) {
    install.packages(pkg, dependencies = TRUE)
    library(pkg, character.only = TRUE)
  }
}

# setup directories and files
BASE_DIRECTORY <- "c:/Github/ils-clustering-hmd/data/clustering/metrics/"
#BASE_DIRECTORY <- "c:/users/marci/desktop/codigos/ils-clustering-hmd/data/clustering/metrics/"
METRICS_RESULTS <- paste0(BASE_DIRECTORY, "metrics_results.csv")
RESULTS <- paste0(BASE_DIRECTORY, "results.csv")
RESULTS_CRG <- paste0(BASE_DIRECTORY, "input/results_crg.csv")
RESULTS_NUMBER_OF_PACKAGES <- paste0(BASE_DIRECTORY, "results_number_of_packages.csv")
RESULTS_NUMBER_OF_EDGES <- paste0(BASE_DIRECTORY, "results_number_of_edges.csv")
RESULTS_AVG_CLASSES <- paste0(BASE_DIRECTORY, "results_avg_classes.csv")
RESULTS_COMMIT_RATIO <- paste0(BASE_DIRECTORY, "results_commit_ratio.csv")
RESULTS_AVG_PACKAGE_COMMIT <- paste0(BASE_DIRECTORY, "results_avg_package_commit.csv")
RESULTS_DISTANCE_TREE <- paste0(BASE_DIRECTORY, "results_distance_tree.csv")
RESULTS_NUMBER_OF_PACKAGES_NORM <- paste0(BASE_DIRECTORY, "results_number_of_packages_norm.csv")
RESULTS_NUMBER_OF_EDGES_NORM <- paste0(BASE_DIRECTORY, "results_number_of_edges_norm.csv")
RESULTS_AVG_CLASSES_NORM <- paste0(BASE_DIRECTORY, "results_avg_classes_norm.csv")
RESULTS_COMMIT_RATIO_NORM <- paste0(BASE_DIRECTORY, "results_commit_ratio_norm.csv")
RESULTS_AVG_PACKAGE_COMMIT_NORM <- paste0(BASE_DIRECTORY, "results_avg_package_commit_norm.csv")
RESULTS_DISTANCE_TREE_HEATMAP <- paste0(BASE_DIRECTORY, "results_distance_tree_heatmap_norm.csv")
RESULTS_MOVEMENTS_PER_CLASS <- paste0(BASE_DIRECTORY, "results_movements_per_class.csv")
RESULTS_KRUSKAL_WALLIS <- paste0(BASE_DIRECTORY, "results_kruskal_wallis.csv")
RESULTS_NORMALIZED_TESTS <- paste0(BASE_DIRECTORY, "results_normalized_zssn_tests.csv")
RESULTS_NORMALIZED_PAIRWISE <- paste0(BASE_DIRECTORY, "results_normalized_zssn_pairwise.csv")
ALL_DATA <- paste0(BASE_DIRECTORY, "all_data.csv")

# Create output directory for plots
plots_dir <- paste0(BASE_DIRECTORY, "plots/")
if (!dir.exists(plots_dir)) {
  dir.create(plots_dir, recursive = TRUE)
}

# eliminate instances list for CRG
eliminated_projects <- c(
  "lucene-luke-11.0.0",
  "joda-time-2.14.0",
  "lucene-analysis-kuromoji-11.0.0",
  "jgit-ssh-6.8.0",
  "jmetal-auto-6.2.2",
  "jmetal-component-6.2.2",
  "moquette-broker-0.19",
  "scribejava-apis-8.3.4",
  "dubbo-cluster-3.2.8",
  "gson-2.10.1",
  "joda-money-marlon"
)

acronyms <- c(
  "javacc-7.0.14" = "JCC",
  "elasticsearch-geo-9.1.0" = "ELG",
  "log4j-iostreams-2.21.1" = "LJI",
  "lucene-highlighter-11.0.0" = "LUH",
  "log4j-layout-template-json-2.21.1" = "LJL",
  "lucene-analysis-nori-11.0.0" = "LUN",
  "elasticsearch-native-9.1.0" = "ELN",
  "log4j-api-2.21.1" = "LJA",
  "jgit-http-6.8.0" = "JGH",
  "javaGeom-0.11.3" = "JGE",
  "elasticsearch-core-9.1.0" = "ELC",
  "jgit-lfs-6.8.0" = "JGL",
  "jmetal-lab-6.2.2" = "JML",
  "lucene-codecs-11.0.0" = "LUC",
  "junit-platform-launcher-1.10.1" = "JPL",
  "log4j-jpa-2.21.1" = "LJJ",
  "joda-money-2.0.3" = "JMO",
  "aep-core-0.10.0" = "AEP",
  "jgit-pgm-6.8.0" = "JGP",
  "junit-jupiter-engine-5.10.1" = "JJE",
  "junit-platform-engine-1.10.1" = "JPE"
)

# Read both tables
structural_metrics_raw <- read_delim(METRICS_RESULTS, delim = ",", col_types = cols(
  projectName = col_character(),
  algorithm = col_character(),
  numberOfNodes = col_integer(),
  numberOfEdges = col_integer(),
  numberOfPackages = col_integer(),
  avgClassesPerPackage = col_double(),
  singlePackageCommitRatio = col_double(),
  avgPackageCommit = col_double(),
  .default = col_skip()
)) %>%
  mutate(
    projectName = str_replace(projectName, "^_b01-", ""),
    projectName = str_replace(projectName, " \\d+C\\.odem$", "")
  ) %>%
  filter(!projectName %in% eliminated_projects) %>%
  rename(compare_type = algorithm)

# Get Original summary (single deterministic result per project)
original_summary <- structural_metrics_raw %>%
  filter(compare_type == "original") %>%
  select(
    projectName,
    original_numberOfNodes = numberOfNodes,
    original_numberOfEdges = numberOfEdges,
    original_numberOfPackages = numberOfPackages,
    original_avgClassesPerPackage = avgClassesPerPackage, 
    original_singlePackageCommitRatio = singlePackageCommitRatio,
    original_avgPackageCommit = avgPackageCommit
  )

zss_results_raw <- read_delim(RESULTS, delim = ",", col_types = cols(
  project = col_character(),
  baseline_type = col_character(),
  compare_type = col_character(),
  distance = col_double(), 
  normalized_distance = col_double(),
  .default = col_skip()
)) %>%
  rename(projectName = project)

# Load ZSS distance results from the notebook analysis
zss_structural_metrics <- zss_results_raw %>%
  # Join structural metrics to every algorithm result (not just original)
  left_join(structural_metrics_raw, by = c("projectName", "compare_type")) %>%
  filter(!projectName %in% eliminated_projects) %>%
  # Clean up the data types and add algorithm type parsing
  mutate(
    # Extract algorithm type from compare_type
    algorithm = case_when(
      compare_type == "hmd" ~ "hmd",
      str_starts(compare_type, "crg_stddev_") ~ "crg",
      str_starts(compare_type, "mq_iteration_") ~ "mq",
      TRUE ~ "unknown"
    ),
    # Extract specific variant information
    variant = case_when(
      compare_type == "hmd" ~ "single",
      str_starts(compare_type, "crg_stddev_") ~ str_extract(compare_type, "\\d+$"),
      str_starts(compare_type, "mq_iteration_") ~ str_extract(compare_type, "\\d+$"),
      TRUE ~ "unknown"
    )
  )

# Create summary statistics for MQ iterations (since it's non-deterministic)
mq_summary <- zss_structural_metrics %>%
  filter(algorithm == "mq") %>%
  group_by(projectName) %>%
  summarise(
    mq_distance_mean = mean(distance),
    mq_normalized_distance_mean = mean(normalized_distance),
    mq_numberOfEdges_mean = mean(numberOfEdges),
    mq_numberOfPackages_mean = mean(numberOfPackages), 
    mq_avgClassesPerPackage_mean = mean(avgClassesPerPackage),
    mq_singlePackageCommitRatio_mean = mean(singlePackageCommitRatio),
    mq_avgPackageCommit_mean = mean(avgPackageCommit),
    mq_iterations = n(),
    .groups = "drop"
  )

# Get MDL summary (single deterministic result per project)
hmd_summary <- zss_structural_metrics %>%
  filter(algorithm == "hmd") %>%
  select(
    projectName, 
    hmd_distance = distance, 
    hmd_normalized_distance = normalized_distance,
    hmd_numberOfEdges = numberOfEdges,
    hmd_numberOfPackages = numberOfPackages,
    hmd_avgClassesPerPackage = avgClassesPerPackage, 
    hmd_singlePackageCommitRatio = singlePackageCommitRatio,
    hmd_avgPackageCommit = avgPackageCommit
  )

# Load algorithm results
crg_results <- read_delim(RESULTS_CRG, delim = ";", col_types = cols(
  ProjectName = col_character(),
  ExecutionTime = col_character(),
  Memory = col_character(),
  StdDev = col_integer(),
  ClassCount = col_integer(),
  ClustersCount = col_integer(),
  Fitness = col_character(),
  ExecutionAborted = col_logical()
)) %>%
  rename(
    projectName = ProjectName,
    executionTime = ExecutionTime,
    memory = Memory,
    executionAborted = ExecutionAborted
  ) %>%
  mutate(
    projectName = str_replace(projectName, "^_b01-", ""),
    projectName = str_trim(str_replace(projectName, " \\d+C\\.odem$", "")),
    executionTime = str_trim(str_replace(executionTime, "MB$", "")),
    memory = str_trim(str_replace(memory, " ms$", ""))
  ) %>%
  filter(!projectName %in% eliminated_projects) %>%
  mutate(
    projectName = str_replace(projectName, "^_b01-", ""),
    projectName = str_replace(projectName, " \\d+C\\.odem$", ""),
    compare_type = str_c("crg_stddev_", StdDev)
  )

# Create summary for CRG variations (deterministic but with different parameters)
crg_summary <- zss_structural_metrics %>%
  filter(str_starts(compare_type, "crg")) %>%
  left_join(crg_results, by = c("projectName", "compare_type")) %>%
  mutate(
    distance = case_when(
      executionAborted == TRUE ~ NA, 
      TRUE ~ distance
    ),
    normalized_distance = case_when(
      executionAborted == TRUE ~ NA, 
      TRUE ~ normalized_distance
    ),
    numberOfEdges = case_when(
      executionAborted == TRUE ~ NA, 
      TRUE ~ numberOfEdges
    ),
    numberOfPackages = case_when(
      executionAborted == TRUE ~ NA, 
      TRUE ~ numberOfPackages
    ),
    avgClassesPerPackage = case_when(
      executionAborted == TRUE ~ NA, 
      TRUE ~ avgClassesPerPackage
    ),
    singlePackageCommitRatio = case_when(
      executionAborted == TRUE ~ NA, 
      TRUE ~ singlePackageCommitRatio
    ),
    avgPackageCommit = case_when(
      executionAborted == TRUE ~ NA, 
      TRUE ~ avgPackageCommit
    ),
  ) %>%
  rename(
    std = StdDev,
    crg_distance = distance,
    crg_normalized_distance = normalized_distance,
    crg_numberOfEdges = numberOfEdges,
    crg_numberOfPackages = numberOfPackages,
    crg_avgClassesPerPackage = avgClassesPerPackage, 
    crg_singlePackageCommitRatio = singlePackageCommitRatio,
    crg_avgPackageCommit = avgPackageCommit
  )

crg_summary <- crg_summary %>%
  mutate(
    compare_type = str_replace(compare_type, "^crg_stddev_", "")
  ) %>%
  select(
    projectName, 
    compare_type, 
    crg_distance, 
    crg_normalized_distance,
    crg_numberOfEdges,
    crg_numberOfPackages,
    crg_avgClassesPerPackage, 
    crg_singlePackageCommitRatio,
    crg_avgPackageCommit
  ) %>% 
  pivot_wider(
    names_from = compare_type, 
    values_from = c(
      crg_distance, 
      crg_normalized_distance,
      crg_numberOfEdges,
      crg_numberOfPackages,
      crg_avgClassesPerPackage, 
      crg_singlePackageCommitRatio,
      crg_avgPackageCommit
    )
  )

all_data <- original_summary %>%
  left_join(mq_summary, by = "projectName") %>% 
  left_join(hmd_summary, by = "projectName") %>% 
  left_join(crg_summary, by = "projectName")

write_csv(all_data, ALL_DATA)

# ------------------ Metrics --------------------------

results_number_of_packages <- all_data %>%
  mutate(
    projectName = acronyms[projectName],
    mq_numberOfPackages_mean = round(mq_numberOfPackages_mean, 0),
  ) %>%
  select(
    projectName,
    original_numberOfPackages,
    mq_numberOfPackages_mean,
    hmd_numberOfPackages,
    crg_numberOfPackages_25,
    crg_numberOfPackages_50,
    crg_numberOfPackages_75
  ) %>%
  rename(
    NAME = projectName,
    DEV = original_numberOfPackages,
    MQ = mq_numberOfPackages_mean,
    MDL = hmd_numberOfPackages,
    `CRG-25` = crg_numberOfPackages_25,
    `CRG-50` = crg_numberOfPackages_50,
    `CRG-75` = crg_numberOfPackages_75
  ) %>%
  arrange(
    NAME
  )

results_number_of_packages_min <- results_number_of_packages %>%
  summarise(
    name = '',
    min_DEV = round(min(DEV), 0),
    min_MQ = round(min(MQ), 0),
    min_MDL = round(min(MDL), 0),
    min_CRG25 = round(min(`CRG-25`), 0),
    min_CRG50 = round(min(`CRG-50`), 0),
    min_CRG75 = round(min(`CRG-75`), 0)
  )

results_number_of_packages_avg <- results_number_of_packages %>%
  summarise(
    name = '',
    avg_DEV = round(mean(DEV), 0),
    avg_MQ = round(mean(MQ), 0),
    avg_MDL = round(mean(MDL), 0),
    avg_CRG25 = round(mean(`CRG-25`), 0),
    avg_CRG50 = round(mean(`CRG-50`), 0),
    avg_CRG75 = round(mean(`CRG-75`), 0)
  )

results_number_of_packages_std <- results_number_of_packages %>%
  summarise(
    name = '',
    std_DEV = round(sd(DEV), 0),
    std_MQ = round(sd(MQ), 0),
    std_MDL = round(sd(MDL), 0),
    std_CRG25 = round(sd(`CRG-25`), 0),
    std_CRG50 = round(sd(`CRG-50`), 0),
    std_CRG75 = round(sd(`CRG-75`), 0)
  )

results_number_of_packages_max <- results_number_of_packages %>%
  summarise(
    name = '',
    max_DEV = round(max(DEV), 0),
    max_MQ = round(max(MQ), 0),
    max_MDL = round(max(MDL), 0),
    max_CRG25 = round(max(`CRG-25`), 0),
    max_CRG50 = round(max(`CRG-50`), 0),
    max_CRG75 = round(max(`CRG-75`), 0)
  )

# Create boxplot for number of packages
results_number_of_packages_long <- results_number_of_packages %>%
  pivot_longer(
    cols = c(DEV, MQ, MDL, `CRG-25`, `CRG-50`, `CRG-75`),
    names_to = "Algorithm",
    values_to = "NumberOfPackages"
  ) %>%
  mutate(Algorithm = factor(Algorithm, levels = c("DEV", "MQ", "MDL", "CRG-25", "CRG-50", "CRG-75")))

results_number_of_packages_norm_long <- results_number_of_packages_long %>%
  filter(Algorithm != "DEV") %>%
  left_join(
    results_number_of_packages_long %>% filter(Algorithm == "DEV") %>% select(NAME, DEV_val = NumberOfPackages),
    by = "NAME"
  ) %>%
  mutate(Ratio = round(NumberOfPackages / DEV_val, 2))

packages_boxplot <- ggplot(
    results_number_of_packages_norm_long %>% mutate(Algorithm = factor(Algorithm, levels = c("MQ", "MDL", "CRG-25", "CRG-50", "CRG-75"))),
    aes(x = Algorithm, y = Ratio, fill = Algorithm)
  ) +
  geom_boxplot(outlier.shape = 1, notch = FALSE, color = "black", staplewidth = 0.5) +
  scale_fill_manual(values = rep("white", 5)) +
  scale_x_discrete(labels = c("MQ" = "MQ", "MDL" = "HMD", "CRG-25" = expression(HMD[25]^'+'), "CRG-50" = expression(HMD[50]^'+'), "CRG-75" = expression(HMD[75]^'+'))) +
  labs(y = "Number of Packages (Ratio to DEV)") +
  theme_classic() +
  theme(
    plot.background = element_rect(fill = "white", color = NA),
    panel.background = element_rect(fill = "white"),
    legend.position = "none",
    axis.title.x = element_blank(),
    axis.title.y = element_text(size = 14),
    axis.text.x = element_text(size = 13),
    axis.text.y = element_text(size = 12),
    panel.grid.major.y = element_line(color = "gray90"),
    panel.grid.major.x = element_blank()
  )

ggsave(paste0(plots_dir, "packages_boxplot.png"), packages_boxplot, width = 10, height = 6, dpi = 300)
print(packages_boxplot)

write_csv(results_number_of_packages, RESULTS_NUMBER_OF_PACKAGES)
write_csv(results_number_of_packages_min, RESULTS_NUMBER_OF_PACKAGES, na = "NA", append = TRUE)
write_csv(results_number_of_packages_avg, RESULTS_NUMBER_OF_PACKAGES, na = "NA", append = TRUE)
write_csv(results_number_of_packages_std, RESULTS_NUMBER_OF_PACKAGES, na = "NA", append = TRUE)
write_csv(results_number_of_packages_max, RESULTS_NUMBER_OF_PACKAGES, na = "NA", append = TRUE)

write_csv(
  results_number_of_packages_norm_long %>% select(NAME, Algorithm, Ratio) %>% pivot_wider(names_from = Algorithm, values_from = Ratio),
  RESULTS_NUMBER_OF_PACKAGES_NORM
)

build_kruskal_row <- function(metric, test_result) {
  tibble(
    metric = metric,
    statistic = round(as.numeric(unname(test_result$statistic)), 3),
    parameter = round(as.numeric(unname(test_result$parameter)), 3),
    p_value = round(test_result$p.value, 3),
    method = test_result$method,
    data_name = test_result$data.name,
    significant_0_05 = test_result$p.value < 0.05
  )
}

paired_rank_biserial <- function(x, y) {
  diffs <- x - y
  diffs <- diffs[!is.na(diffs) & diffs != 0]

  if (length(diffs) == 0) {
    return(NA_real_)
  }

  ranks <- rank(abs(diffs))
  positive_rank_sum <- sum(ranks[diffs > 0])
  max_rank_sum <- length(diffs) * (length(diffs) + 1) / 2

  round((2 * positive_rank_sum / max_rank_sum) - 1, 3)
}

kruskal_packages <- kruskal.test(NumberOfPackages ~ Algorithm, data = results_number_of_packages_long)
print(kruskal_packages)

packages_heatmap_data <- results_number_of_packages_norm_long %>%
  mutate(
    Algorithm = factor(Algorithm, levels = c("MQ", "MDL", "CRG-25", "CRG-50", "CRG-75")),
    NAME = factor(NAME, levels = sort(unique(NAME), decreasing = TRUE)),
    Deviation = abs(Ratio - 1),
    max_dev = max(abs(Ratio - 1), na.rm = TRUE),
    text_color = ifelse(Deviation / max_dev > 0.5, "white", "black")
  )

packages_heatmap <- ggplot(packages_heatmap_data, aes(x = Algorithm, y = NAME, fill = Deviation)) +
  geom_tile(color = "white", linewidth = 0.5) +
  geom_text(aes(label = sprintf("%.2f", Ratio)), size = 3.5, fontface = "bold",
            color = packages_heatmap_data$text_color) +
  scale_fill_gradient(low = "#F7F7F7", high = "#1A1A1A", trans = "sqrt", name = "Deviation\nfrom DEV") +
  scale_x_discrete(labels = c("MQ" = "MQ", "MDL" = "HMD", "CRG-25" = expression(HMD[25]^"+"), "CRG-50" = expression(HMD[50]^"+"), "CRG-75" = expression(HMD[75]^"+"))) +
  theme_classic() +
  theme(
    plot.background = element_rect(fill = "white", color = NA),
    panel.background = element_rect(fill = "white"),
    axis.title = element_blank(),
    axis.text.x = element_text(size = 12),
    axis.text.y = element_text(size = 11),
    axis.ticks = element_blank(),
    axis.line = element_blank(),
    legend.title = element_text(size = 10),
    legend.text = element_text(size = 9)
  )

ggsave(paste0(plots_dir, "packages_heatmap.png"), packages_heatmap, width = 7, height = 8, dpi = 300)
print(packages_heatmap)

results_number_of_edges <- all_data %>%
  mutate(
    projectName = acronyms[projectName],
    mq_numberOfEdges_mean = round(mq_numberOfEdges_mean, 0),
  ) %>%
  select(
    projectName,
    original_numberOfEdges,
    mq_numberOfEdges_mean,
    hmd_numberOfEdges,
    crg_numberOfEdges_25,
    crg_numberOfEdges_50,
    crg_numberOfEdges_75
  ) %>%
  rename(
    NAME = projectName,
    DEV = original_numberOfEdges,
    MQ = mq_numberOfEdges_mean,
    MDL = hmd_numberOfEdges,
    `CRG-25` = crg_numberOfEdges_25,
    `CRG-50` = crg_numberOfEdges_50,
    `CRG-75` = crg_numberOfEdges_75
  ) %>%
  arrange(
    NAME
  )

results_number_of_edges_min <- results_number_of_edges %>%
  summarise(
    name = '',
    min_DEV = round(min(DEV), 0),
    min_MQ = round(min(MQ), 0),
    min_MDL = round(min(MDL), 0),
    min_CRG25 = round(min(`CRG-25`), 0),
    min_CRG50 = round(min(`CRG-50`), 0),
    min_CRG75 = round(min(`CRG-75`), 0)
  )

results_number_of_edges_avg <- results_number_of_edges %>%
  summarise(
    name = '',
    avg_DEV = round(mean(DEV), 0),
    avg_MQ = round(mean(MQ), 0),
    avg_MDL = round(mean(MDL), 0),
    avg_CRG25 = round(mean(`CRG-25`), 0),
    avg_CRG50 = round(mean(`CRG-50`), 0),
    avg_CRG75 = round(mean(`CRG-75`), 0)
  )

results_number_of_edges_std <- results_number_of_edges %>%
  summarise(
    name = '',
    std_DEV = round(sd(DEV), 0),
    std_MQ = round(sd(MQ), 0),
    std_MDL = round(sd(MDL), 0),
    std_CRG25 = round(sd(`CRG-25`), 0),
    std_CRG50 = round(sd(`CRG-50`), 0),
    std_CRG75 = round(sd(`CRG-75`), 0)
  )

results_number_of_edges_max <- results_number_of_edges %>%
  summarise(
    name = '',
    max_DEV = round(max(DEV), 0),
    max_MQ = round(max(MQ), 0),
    max_MDL = round(max(MDL), 0),
    max_CRG25 = round(max(`CRG-25`), 0),
    max_CRG50 = round(max(`CRG-50`), 0),
    max_CRG75 = round(max(`CRG-75`), 0)
  )

# Create boxplot for number of edges
results_number_of_edges_long <- results_number_of_edges %>%
  pivot_longer(
    cols = c(DEV, MQ, MDL, `CRG-25`, `CRG-50`, `CRG-75`),
    names_to = "Algorithm",
    values_to = "NumberOfEdges"
  ) %>%
  mutate(Algorithm = factor(Algorithm, levels = c("DEV", "MQ", "MDL", "CRG-25", "CRG-50", "CRG-75")))

results_number_of_edges_norm_long <- results_number_of_edges_long %>%
  filter(Algorithm != "DEV") %>%
  left_join(
    results_number_of_edges_long %>% filter(Algorithm == "DEV") %>% select(NAME, DEV_val = NumberOfEdges),
    by = "NAME"
  ) %>%
  mutate(Ratio = round(NumberOfEdges / DEV_val, 2))

edges_boxplot <- ggplot(
    results_number_of_edges_norm_long %>% mutate(Algorithm = factor(Algorithm, levels = c("MQ", "MDL", "CRG-25", "CRG-50", "CRG-75"))),
    aes(x = Algorithm, y = Ratio, fill = Algorithm)
  ) +
  geom_boxplot(outlier.shape = 1, notch = FALSE, color = "black", staplewidth = 0.5) +
  scale_fill_manual(values = rep("white", 5)) +
  scale_x_discrete(labels = c("MQ" = "MQ", "MDL" = "HMD", "CRG-25" = expression(HMD[25]^'+'), "CRG-50" = expression(HMD[50]^'+'), "CRG-75" = expression(HMD[75]^'+'))) +
  labs(y = "Number of Edges (Ratio to DEV)") +
  theme_classic() +
  theme(
    plot.background = element_rect(fill = "white", color = NA),
    panel.background = element_rect(fill = "white"),
    legend.position = "none",
    axis.title.x = element_blank(),
    axis.title.y = element_text(size = 14),
    axis.text.x = element_text(size = 13),
    axis.text.y = element_text(size = 12),
    panel.grid.major.y = element_line(color = "gray90"),
    panel.grid.major.x = element_blank()
  )

ggsave(paste0(plots_dir, "edges_boxplot.png"), edges_boxplot, width = 10, height = 6, dpi = 300)
print(edges_boxplot)

write_csv(results_number_of_edges, RESULTS_NUMBER_OF_EDGES)
write_csv(results_number_of_edges_min, RESULTS_NUMBER_OF_EDGES, na = "NA", append = TRUE)
write_csv(results_number_of_edges_avg, RESULTS_NUMBER_OF_EDGES, na = "NA", append = TRUE)
write_csv(results_number_of_edges_std, RESULTS_NUMBER_OF_EDGES, na = "NA", append = TRUE)
write_csv(results_number_of_edges_max, RESULTS_NUMBER_OF_EDGES, na = "NA", append = TRUE)

write_csv(
  results_number_of_edges_norm_long %>% select(NAME, Algorithm, Ratio) %>% pivot_wider(names_from = Algorithm, values_from = Ratio),
  RESULTS_NUMBER_OF_EDGES_NORM
)

kruskal_edges <- kruskal.test(NumberOfEdges ~ Algorithm, data = results_number_of_edges_long)
print(kruskal_edges)

edges_heatmap_data <- results_number_of_edges_norm_long %>%
  mutate(
    Algorithm = factor(Algorithm, levels = c("MQ", "MDL", "CRG-25", "CRG-50", "CRG-75")),
    NAME = factor(NAME, levels = sort(unique(NAME), decreasing = TRUE)),
    Deviation = abs(Ratio - 1),
    max_dev = max(abs(Ratio - 1), na.rm = TRUE),
    text_color = ifelse(Deviation / max_dev > 0.5, "white", "black")
  )

edges_heatmap <- ggplot(edges_heatmap_data, aes(x = Algorithm, y = NAME, fill = Deviation)) +
  geom_tile(color = "white", linewidth = 0.5) +
  geom_text(aes(label = sprintf("%.2f", Ratio)), size = 3.5, fontface = "bold",
            color = edges_heatmap_data$text_color) +
  scale_fill_gradient(low = "#F7F7F7", high = "#1A1A1A", trans = "sqrt", name = "Deviation\nfrom DEV") +
  scale_x_discrete(labels = c("MQ" = "MQ", "MDL" = "HMD", "CRG-25" = expression(HMD[25]^"+"), "CRG-50" = expression(HMD[50]^"+"), "CRG-75" = expression(HMD[75]^"+"))) +
  theme_classic() +
  theme(
    plot.background = element_rect(fill = "white", color = NA),
    panel.background = element_rect(fill = "white"),
    axis.title = element_blank(),
    axis.text.x = element_text(size = 12),
    axis.text.y = element_text(size = 11),
    axis.ticks = element_blank(),
    axis.line = element_blank(),
    legend.title = element_text(size = 10),
    legend.text = element_text(size = 9)
  )

ggsave(paste0(plots_dir, "edges_heatmap.png"), edges_heatmap, width = 7, height = 8, dpi = 300)
print(edges_heatmap)

results_avgClassesPerPackage <- all_data %>%
  mutate(
    projectName = acronyms[projectName],
    original_avgClassesPerPackage = round(original_avgClassesPerPackage, 1),
    mq_avgClassesPerPackage_mean = round(mq_avgClassesPerPackage_mean, 1),
    hmd_avgClassesPerPackage = round(hmd_avgClassesPerPackage, 1),
    crg_avgClassesPerPackage_25 = round(crg_avgClassesPerPackage_25, 1),
    crg_avgClassesPerPackage_50 = round(crg_avgClassesPerPackage_50, 1),
    crg_avgClassesPerPackage_75 = round(crg_avgClassesPerPackage_75, 1),
  ) %>%
  select(
    projectName,
    original_avgClassesPerPackage,
    mq_avgClassesPerPackage_mean,
    hmd_avgClassesPerPackage,
    crg_avgClassesPerPackage_25,
    crg_avgClassesPerPackage_50,
    crg_avgClassesPerPackage_75
  ) %>%
  rename(
    NAME = projectName,
    DEV = original_avgClassesPerPackage,
    MQ = mq_avgClassesPerPackage_mean,
    MDL = hmd_avgClassesPerPackage,
    `CRG-25` = crg_avgClassesPerPackage_25,
    `CRG-50` = crg_avgClassesPerPackage_50,
    `CRG-75` = crg_avgClassesPerPackage_75
  ) %>%
  arrange(
    NAME
  )

results_avgClassesPerPackage_min <- results_avgClassesPerPackage %>%
  summarise(
    name = '',
    min_DEV = round(min(DEV), 1),
    min_MQ = round(min(MQ), 1),
    min_MDL = round(min(MDL), 1),
    min_CRG25 = round(min(`CRG-25`), 1),
    min_CRG50 = round(min(`CRG-50`), 1),
    min_CRG75 = round(min(`CRG-75`), 1)
  )

results_avgClassesPerPackage_avg <- results_avgClassesPerPackage %>%
  summarise(
    name = '',
    avg_DEV = round(mean(DEV), 1),
    avg_MQ = round(mean(MQ), 1),
    avg_MDL = round(mean(MDL), 1),
    avg_CRG25 = round(mean(`CRG-25`), 1),
    avg_CRG50 = round(mean(`CRG-50`), 1),
    avg_CRG75 = round(mean(`CRG-75`), 1)
  )

results_avgClassesPerPackage_std <- results_avgClassesPerPackage %>%
  summarise(
    name = '',
    std_DEV = round(sd(DEV), 1),
    std_MQ = round(sd(MQ), 1),
    std_MDL = round(sd(MDL), 1),
    std_CRG25 = round(sd(`CRG-25`), 1),
    std_CRG50 = round(sd(`CRG-50`), 1),
    std_CRG75 = round(sd(`CRG-75`), 1)
  )

results_avgClassesPerPackage_max <- results_avgClassesPerPackage %>%
  summarise(
    name = '',
    max_DEV = round(max(DEV), 1),
    max_MQ = round(max(MQ), 1),
    max_MDL = round(max(MDL), 1),
    max_CRG25 = round(max(`CRG-25`), 1),
    max_CRG50 = round(max(`CRG-50`), 1),
    max_CRG75 = round(max(`CRG-75`), 1)
  )

# Create boxplot for average classes per package
results_avgClassesPerPackage_long <- results_avgClassesPerPackage %>%
  pivot_longer(
    cols = c(DEV, MQ, MDL, `CRG-25`, `CRG-50`, `CRG-75`),
    names_to = "Algorithm",
    values_to = "AvgClassesPerPackage"
  ) %>%
  mutate(Algorithm = factor(Algorithm, levels = c("DEV", "MQ", "MDL", "CRG-25", "CRG-50", "CRG-75")))

results_avgClassesPerPackage_norm_long <- results_avgClassesPerPackage_long %>%
  filter(Algorithm != "DEV") %>%
  left_join(
    results_avgClassesPerPackage_long %>% filter(Algorithm == "DEV") %>% select(NAME, DEV_val = AvgClassesPerPackage),
    by = "NAME"
  ) %>%
  mutate(Ratio = round(AvgClassesPerPackage / DEV_val, 2))

avgclasses_boxplot <- ggplot(
    results_avgClassesPerPackage_norm_long %>% mutate(Algorithm = factor(Algorithm, levels = c("MQ", "MDL", "CRG-25", "CRG-50", "CRG-75"))),
    aes(x = Algorithm, y = Ratio, fill = Algorithm)
  ) +
  geom_boxplot(outlier.shape = 1, notch = FALSE, color = "black", staplewidth = 0.5) +
  scale_fill_manual(values = rep("white", 5)) +
  scale_x_discrete(labels = c("MQ" = "MQ", "MDL" = "HMD", "CRG-25" = expression(HMD[25]^'+'), "CRG-50" = expression(HMD[50]^'+'), "CRG-75" = expression(HMD[75]^'+'))) +
  labs(y = "Average Classes per Package (Ratio to DEV)") +
  theme_classic() +
  theme(
    plot.background = element_rect(fill = "white", color = NA),
    panel.background = element_rect(fill = "white"),
    legend.position = "none",
    axis.title.x = element_blank(),
    axis.title.y = element_text(size = 14),
    axis.text.x = element_text(size = 13),
    axis.text.y = element_text(size = 12),
    panel.grid.major.y = element_line(color = "gray90"),
    panel.grid.major.x = element_blank()
  )

ggsave(paste0(plots_dir, "avgclasses_boxplot.png"), avgclasses_boxplot, width = 10, height = 6, dpi = 300)
print(avgclasses_boxplot)

write_csv(results_avgClassesPerPackage, RESULTS_AVG_CLASSES)
write_csv(results_avgClassesPerPackage_min, RESULTS_AVG_CLASSES, na = "NA", append = TRUE)
write_csv(results_avgClassesPerPackage_avg, RESULTS_AVG_CLASSES, na = "NA", append = TRUE)
write_csv(results_avgClassesPerPackage_std, RESULTS_AVG_CLASSES, na = "NA", append = TRUE)
write_csv(results_avgClassesPerPackage_max, RESULTS_AVG_CLASSES, na = "NA", append = TRUE)

write_csv(
  results_avgClassesPerPackage_norm_long %>% select(NAME, Algorithm, Ratio) %>% pivot_wider(names_from = Algorithm, values_from = Ratio),
  RESULTS_AVG_CLASSES_NORM
)

kruskal_avgClasses <- kruskal.test(AvgClassesPerPackage ~ Algorithm, data = results_avgClassesPerPackage_long)
print(kruskal_avgClasses)

avgclasses_heatmap_data <- results_avgClassesPerPackage_norm_long %>%
  mutate(
    Algorithm = factor(Algorithm, levels = c("MQ", "MDL", "CRG-25", "CRG-50", "CRG-75")),
    NAME = factor(NAME, levels = sort(unique(NAME), decreasing = TRUE)),
    Deviation = abs(Ratio - 1),
    max_dev = max(abs(Ratio - 1), na.rm = TRUE),
    text_color = ifelse(Deviation / max_dev > 0.5, "white", "black")
  )

avgclasses_heatmap <- ggplot(avgclasses_heatmap_data, aes(x = Algorithm, y = NAME, fill = Deviation)) +
  geom_tile(color = "white", linewidth = 0.5) +
  geom_text(aes(label = sprintf("%.2f", Ratio)), size = 3.5, fontface = "bold",
            color = avgclasses_heatmap_data$text_color) +
  scale_fill_gradient(low = "#F7F7F7", high = "#1A1A1A", trans = "sqrt", name = "Deviation\nfrom DEV") +
  scale_x_discrete(labels = c("MQ" = "MQ", "MDL" = "HMD", "CRG-25" = expression(HMD[25]^"+"), "CRG-50" = expression(HMD[50]^"+"), "CRG-75" = expression(HMD[75]^"+"))) +
  theme_classic() +
  theme(
    plot.background = element_rect(fill = "white", color = NA),
    panel.background = element_rect(fill = "white"),
    axis.title = element_blank(),
    axis.text.x = element_text(size = 12),
    axis.text.y = element_text(size = 11),
    axis.ticks = element_blank(),
    axis.line = element_blank(),
    legend.title = element_text(size = 10),
    legend.text = element_text(size = 9)
  )

ggsave(paste0(plots_dir, "avgclasses_heatmap.png"), avgclasses_heatmap, width = 7, height = 8, dpi = 300)
print(avgclasses_heatmap)

results_singlePackageCommitRatio <- all_data %>%
  mutate(
    projectName = acronyms[projectName],
    original_singlePackageCommitRatio = round(original_singlePackageCommitRatio, 2),
    mq_singlePackageCommitRatio_mean = round(mq_singlePackageCommitRatio_mean, 2),
    hmd_singlePackageCommitRatio = round(hmd_singlePackageCommitRatio, 2),
    crg_singlePackageCommitRatio_25 = round(crg_singlePackageCommitRatio_25, 2),
    crg_singlePackageCommitRatio_50 = round(crg_singlePackageCommitRatio_50, 2),
    crg_singlePackageCommitRatio_75 = round(crg_singlePackageCommitRatio_75, 2),
  ) %>%
  select(
    projectName,
    original_singlePackageCommitRatio,
    mq_singlePackageCommitRatio_mean,
    hmd_singlePackageCommitRatio,
    crg_singlePackageCommitRatio_25,
    crg_singlePackageCommitRatio_50,
    crg_singlePackageCommitRatio_75
  ) %>%
  rename(
    NAME = projectName,
    DEV = original_singlePackageCommitRatio,
    MQ = mq_singlePackageCommitRatio_mean,
    MDL = hmd_singlePackageCommitRatio,
    `CRG-25` = crg_singlePackageCommitRatio_25,
    `CRG-50` = crg_singlePackageCommitRatio_50,
    `CRG-75` = crg_singlePackageCommitRatio_75
  ) %>%
  arrange(
    NAME
  )

results_singlePackageCommitRatio_min <- results_singlePackageCommitRatio %>%
  summarise(
    name = '',
    min_DEV = round(min(DEV), 2),
    min_MQ = round(min(MQ), 2),
    min_MDL = round(min(MDL), 2),
    min_CRG25 = round(min(`CRG-25`), 2),
    min_CRG50 = round(min(`CRG-50`), 2),
    min_CRG75 = round(min(`CRG-75`), 2)
  )

results_singlePackageCommitRatio_avg <- results_singlePackageCommitRatio %>%
  summarise(
    name = '',
    avg_DEV = round(mean(DEV), 2),
    avg_MQ = round(mean(MQ), 2),
    avg_MDL = round(mean(MDL), 2),
    avg_CRG25 = round(mean(`CRG-25`), 2),
    avg_CRG50 = round(mean(`CRG-50`), 2),
    avg_CRG75 = round(mean(`CRG-75`), 2)
  )

results_singlePackageCommitRatio_std <- results_singlePackageCommitRatio %>%
  summarise(
    name = '',
    std_DEV = round(sd(DEV), 2),
    std_MQ = round(sd(MQ), 2),
    std_MDL = round(sd(MDL), 2),
    std_CRG25 = round(sd(`CRG-25`), 2),
    std_CRG50 = round(sd(`CRG-50`), 2),
    std_CRG75 = round(sd(`CRG-75`), 2)
  )

results_singlePackageCommitRatio_max <- results_singlePackageCommitRatio %>%
  summarise(
    name = '',
    max_DEV = round(max(DEV), 2),
    max_MQ = round(max(MQ), 2),
    max_MDL = round(max(MDL), 2),
    max_CRG25 = round(max(`CRG-25`), 2),
    max_CRG50 = round(max(`CRG-50`), 2),
    max_CRG75 = round(max(`CRG-75`), 2)
  )

# Create boxplot for single package commit ratio
results_singlePackageCommitRatio_long <- results_singlePackageCommitRatio %>%
  pivot_longer(
    cols = c(DEV, MQ, MDL, `CRG-25`, `CRG-50`, `CRG-75`),
    names_to = "Algorithm",
    values_to = "SinglePackageCommitRatio"
  ) %>%
  mutate(Algorithm = factor(Algorithm, levels = c("DEV", "MQ", "MDL", "CRG-25", "CRG-50", "CRG-75")))

results_singlePackageCommitRatio_norm_long <- results_singlePackageCommitRatio_long %>%
  filter(Algorithm != "DEV") %>%
  left_join(
    results_singlePackageCommitRatio_long %>% filter(Algorithm == "DEV") %>% select(NAME, DEV_val = SinglePackageCommitRatio),
    by = "NAME"
  ) %>%
  mutate(Ratio = round(SinglePackageCommitRatio / DEV_val, 2))

commit_ratio_boxplot <- ggplot(
    results_singlePackageCommitRatio_norm_long %>% mutate(Algorithm = factor(Algorithm, levels = c("MQ", "MDL", "CRG-25", "CRG-50", "CRG-75"))),
    aes(x = Algorithm, y = Ratio, fill = Algorithm)
  ) +
  geom_boxplot(outlier.shape = 1, notch = FALSE, color = "black", staplewidth = 0.5) +
  scale_fill_manual(values = rep("white", 5)) +
  scale_x_discrete(labels = c("MQ" = "MQ", "MDL" = "HMD", "CRG-25" = expression(HMD[25]^'+'), "CRG-50" = expression(HMD[50]^'+'), "CRG-75" = expression(HMD[75]^'+'))) +
  labs(y = "Single Package Commit Ratio (Ratio to DEV)") +
  theme_classic() +
  theme(
    plot.background = element_rect(fill = "white", color = NA),
    panel.background = element_rect(fill = "white"),
    legend.position = "none",
    axis.title.x = element_blank(),
    axis.title.y = element_text(size = 14),
    axis.text.x = element_text(size = 13),
    axis.text.y = element_text(size = 12),
    panel.grid.major.y = element_line(color = "gray90"),
    panel.grid.major.x = element_blank()
  )

ggsave(paste0(plots_dir, "commit_ratio_boxplot.png"), commit_ratio_boxplot, width = 10, height = 6, dpi = 300)
print(commit_ratio_boxplot)

write_csv(results_singlePackageCommitRatio, RESULTS_COMMIT_RATIO)
write_csv(results_singlePackageCommitRatio_min, RESULTS_COMMIT_RATIO, na = "NA", append = TRUE)
write_csv(results_singlePackageCommitRatio_avg, RESULTS_COMMIT_RATIO, na = "NA", append = TRUE)
write_csv(results_singlePackageCommitRatio_std, RESULTS_COMMIT_RATIO, na = "NA", append = TRUE)
write_csv(results_singlePackageCommitRatio_max, RESULTS_COMMIT_RATIO, na = "NA", append = TRUE)

write_csv(
  results_singlePackageCommitRatio_norm_long %>% select(NAME, Algorithm, Ratio) %>% pivot_wider(names_from = Algorithm, values_from = Ratio),
  RESULTS_COMMIT_RATIO_NORM
)

kruskal_commitRatio <- kruskal.test(SinglePackageCommitRatio ~ Algorithm, data = results_singlePackageCommitRatio_long)
print(kruskal_commitRatio)

commit_ratio_heatmap_data <- results_singlePackageCommitRatio_norm_long %>%
  mutate(
    Algorithm = factor(Algorithm, levels = c("MQ", "MDL", "CRG-25", "CRG-50", "CRG-75")),
    NAME = factor(NAME, levels = sort(unique(NAME), decreasing = TRUE)),
    Deviation = abs(Ratio - 1),
    max_dev = max(abs(Ratio - 1), na.rm = TRUE),
    text_color = ifelse(Deviation / max_dev > 0.5, "white", "black")
  )

commit_ratio_heatmap <- ggplot(commit_ratio_heatmap_data, aes(x = Algorithm, y = NAME, fill = Deviation)) +
  geom_tile(color = "white", linewidth = 0.5) +
  geom_text(aes(label = sprintf("%.2f", Ratio)), size = 3.5, fontface = "bold",
            color = commit_ratio_heatmap_data$text_color) +
  scale_fill_gradient(
    low = "#F7F7F7", high = "#1A1A1A", trans = "sqrt",
    name = "Deviation\nfrom DEV"
  ) +
  scale_x_discrete(labels = c("MQ" = "MQ", "MDL" = "HMD", "CRG-25" = expression(HMD[25]^"+"), "CRG-50" = expression(HMD[50]^"+"), "CRG-75" = expression(HMD[75]^"+"))) +
  theme_classic() +
  theme(
    plot.background = element_rect(fill = "white", color = NA),
    panel.background = element_rect(fill = "white"),
    axis.title = element_blank(),
    axis.text.x = element_text(size = 12),
    axis.text.y = element_text(size = 11),
    axis.ticks = element_blank(),
    axis.line = element_blank(),
    legend.title = element_text(size = 10),
    legend.text = element_text(size = 9)
  )

ggsave(paste0(plots_dir, "commit_ratio_heatmap.png"), commit_ratio_heatmap, width = 7, height = 8, dpi = 300)
print(commit_ratio_heatmap)

results_avgPackageCommit <- all_data %>%
  mutate(
    projectName = acronyms[projectName],
    original_avgPackageCommit = round(original_avgPackageCommit, 2),
    mq_avgPackageCommit_mean = round(mq_avgPackageCommit_mean, 2),
    hmd_avgPackageCommit = round(hmd_avgPackageCommit, 2),
    crg_avgPackageCommit_25 = round(crg_avgPackageCommit_25, 2),
    crg_avgPackageCommit_50 = round(crg_avgPackageCommit_50, 2),
    crg_avgPackageCommit_75 = round(crg_avgPackageCommit_75, 2),
  ) %>%
  select(
    projectName,
    original_avgPackageCommit,
    mq_avgPackageCommit_mean,
    hmd_avgPackageCommit,
    crg_avgPackageCommit_25,
    crg_avgPackageCommit_50,
    crg_avgPackageCommit_75
  ) %>%
  rename(
    NAME = projectName,
    DEV = original_avgPackageCommit,
    MQ = mq_avgPackageCommit_mean,
    MDL = hmd_avgPackageCommit,
    `CRG-25` = crg_avgPackageCommit_25,
    `CRG-50` = crg_avgPackageCommit_50,
    `CRG-75` = crg_avgPackageCommit_75
  ) %>%
  arrange(
    NAME
  )

results_avgPackageCommit_min <- results_avgPackageCommit %>%
  summarise(
    name = '',
    min_DEV = round(min(DEV), 2),
    min_MQ = round(min(MQ), 2),
    min_MDL = round(min(MDL), 2),
    min_CRG25 = round(min(`CRG-25`), 2),
    min_CRG50 = round(min(`CRG-50`), 2),
    min_CRG75 = round(min(`CRG-75`), 2)
  )

results_avgPackageCommit_avg <- results_avgPackageCommit %>%
  summarise(
    name = '',
    avg_DEV = round(mean(DEV), 2),
    avg_MQ = round(mean(MQ), 2),
    avg_MDL = round(mean(MDL), 2),
    avg_CRG25 = round(mean(`CRG-25`), 2),
    avg_CRG50 = round(mean(`CRG-50`), 2),
    avg_CRG75 = round(mean(`CRG-75`), 2)
  )

results_avgPackageCommit_std <- results_avgPackageCommit %>%
  summarise(
    name = '',
    std_DEV = round(sd(DEV), 2),
    std_MQ = round(sd(MQ), 2),
    std_MDL = round(sd(MDL), 2),
    std_CRG25 = round(sd(`CRG-25`), 2),
    std_CRG50 = round(sd(`CRG-50`), 2),
    std_CRG75 = round(sd(`CRG-75`), 2)
  )

results_avgPackageCommit_max <- results_avgPackageCommit %>%
  summarise(
    name = '',
    max_DEV = round(max(DEV), 2),
    max_MQ = round(max(MQ), 2),
    max_MDL = round(max(MDL), 2),
    max_CRG25 = round(max(`CRG-25`), 2),
    max_CRG50 = round(max(`CRG-50`), 2),
    max_CRG75 = round(max(`CRG-75`), 2)
  )

# Create boxplot for average package commit
results_avgPackageCommit_long <- results_avgPackageCommit %>%
  pivot_longer(
    cols = c(DEV, MQ, MDL, `CRG-25`, `CRG-50`, `CRG-75`),
    names_to = "Algorithm",
    values_to = "AvgPackageCommit"
  ) %>%
  mutate(Algorithm = factor(Algorithm, levels = c("DEV", "MQ", "MDL", "CRG-25", "CRG-50", "CRG-75")))

results_avgPackageCommit_norm_long <- results_avgPackageCommit_long %>%
  filter(Algorithm != "DEV") %>%
  left_join(
    results_avgPackageCommit_long %>% filter(Algorithm == "DEV") %>% select(NAME, DEV_val = AvgPackageCommit),
    by = "NAME"
  ) %>%
  mutate(Ratio = round(AvgPackageCommit / DEV_val, 2))

avgpackage_commit_boxplot <- ggplot(
    results_avgPackageCommit_norm_long %>% mutate(Algorithm = factor(Algorithm, levels = c("MQ", "MDL", "CRG-25", "CRG-50", "CRG-75"))),
    aes(x = Algorithm, y = Ratio, fill = Algorithm)
  ) +
  geom_boxplot(outlier.shape = 1, notch = FALSE, color = "black", staplewidth = 0.5) +
  scale_fill_manual(values = rep("white", 5)) +
  scale_x_discrete(labels = c("MQ" = "MQ", "MDL" = "HMD", "CRG-25" = expression(HMD[25]^'+'), "CRG-50" = expression(HMD[50]^'+'), "CRG-75" = expression(HMD[75]^'+'))) +
  labs(y = "Average Package Commit (Ratio to DEV)") +
  theme_classic() +
  theme(
    plot.background = element_rect(fill = "white", color = NA),
    panel.background = element_rect(fill = "white"),
    legend.position = "none",
    axis.title.x = element_blank(),
    axis.title.y = element_text(size = 14),
    axis.text.x = element_text(size = 13),
    axis.text.y = element_text(size = 12),
    panel.grid.major.y = element_line(color = "gray90"),
    panel.grid.major.x = element_blank()
  )

ggsave(paste0(plots_dir, "avgpackage_commit_boxplot.png"), avgpackage_commit_boxplot, width = 10, height = 6, dpi = 300)
print(avgpackage_commit_boxplot)

write_csv(results_avgPackageCommit, RESULTS_AVG_PACKAGE_COMMIT)
write_csv(results_avgPackageCommit_min, RESULTS_AVG_PACKAGE_COMMIT, na = "NA", append = TRUE)
write_csv(results_avgPackageCommit_avg, RESULTS_AVG_PACKAGE_COMMIT, na = "NA", append = TRUE)
write_csv(results_avgPackageCommit_std, RESULTS_AVG_PACKAGE_COMMIT, na = "NA", append = TRUE)
write_csv(results_avgPackageCommit_max, RESULTS_AVG_PACKAGE_COMMIT, na = "NA", append = TRUE)

write_csv(
  results_avgPackageCommit_norm_long %>% select(NAME, Algorithm, Ratio) %>% pivot_wider(names_from = Algorithm, values_from = Ratio),
  RESULTS_AVG_PACKAGE_COMMIT_NORM
)

kruskal_avgPackageCommit <- kruskal.test(AvgPackageCommit ~ Algorithm, data = results_avgPackageCommit_long)
print(kruskal_avgPackageCommit)

avgpackage_commit_heatmap_data <- results_avgPackageCommit_norm_long %>%
  mutate(
    Algorithm = factor(Algorithm, levels = c("MQ", "MDL", "CRG-25", "CRG-50", "CRG-75")),
    NAME = factor(NAME, levels = sort(unique(NAME), decreasing = TRUE)),
    Deviation = abs(Ratio - 1),
    max_dev = max(abs(Ratio - 1), na.rm = TRUE),
    text_color = ifelse(Deviation / max_dev > 0.5, "white", "black")
  )

avgpackage_commit_heatmap <- ggplot(avgpackage_commit_heatmap_data, aes(x = Algorithm, y = NAME, fill = Deviation)) +
  geom_tile(color = "white", linewidth = 0.5) +
  geom_text(aes(label = sprintf("%.2f", Ratio)), size = 3.5, fontface = "bold",
            color = avgpackage_commit_heatmap_data$text_color) +
  scale_fill_gradient(low = "#F7F7F7", high = "#1A1A1A", trans = "sqrt", name = "Deviation\nfrom DEV") +
  scale_x_discrete(labels = c("MQ" = "MQ", "MDL" = "HMD", "CRG-25" = expression(HMD[25]^"+"), "CRG-50" = expression(HMD[50]^"+"), "CRG-75" = expression(HMD[75]^"+"))) +
  theme_classic() +
  theme(
    plot.background = element_rect(fill = "white", color = NA),
    panel.background = element_rect(fill = "white"),
    axis.title = element_blank(),
    axis.text.x = element_text(size = 12),
    axis.text.y = element_text(size = 11),
    axis.ticks = element_blank(),
    axis.line = element_blank(),
    legend.title = element_text(size = 10),
    legend.text = element_text(size = 9)
  )

ggsave(paste0(plots_dir, "avgpackage_commit_heatmap.png"), avgpackage_commit_heatmap, width = 7, height = 8, dpi = 300)
print(avgpackage_commit_heatmap)

results_distance_tree <- all_data %>%
  mutate(
    projectName = acronyms[projectName],
    mq_distance_mean = round(mq_distance_mean, 0),
  ) %>%
  select(
    projectName,
    mq_distance_mean,
    hmd_distance,
    crg_distance_25,
    crg_distance_50,
    crg_distance_75
  ) %>%
  rename(
    NAME = projectName,
    MQ = mq_distance_mean,
    MDL = hmd_distance,
    `CRG-25` = crg_distance_25,
    `CRG-50` = crg_distance_50,
    `CRG-75` = crg_distance_75
  ) %>%
  arrange(
    NAME
  )

results_distance_tree_min <- results_distance_tree %>%
  summarise(
    name = '',
    min_MQ = round(min(MQ), 0),
    min_MDL = round(min(MDL), 0),
    min_CRG25 = round(min(`CRG-25`), 0),
    min_CRG50 = round(min(`CRG-50`), 0),
    min_CRG75 = round(min(`CRG-75`), 0)
  )

results_distance_tree_avg <- results_distance_tree %>%
  summarise(
    name = '',
    avg_MQ = round(mean(MQ), 0),
    avg_MDL = round(mean(MDL), 0),
    avg_CRG25 = round(mean(`CRG-25`), 0),
    avg_CRG50 = round(mean(`CRG-50`), 0),
    avg_CRG75 = round(mean(`CRG-75`), 0)
  )

results_distance_tree_std <- results_distance_tree %>%
  summarise(
    name = '',
    std_MQ = round(sd(MQ), 0),
    std_MDL = round(sd(MDL), 0),
    std_CRG25 = round(sd(`CRG-25`), 0),
    std_CRG50 = round(sd(`CRG-50`), 0),
    std_CRG75 = round(sd(`CRG-75`), 0)
  )

results_distance_tree_max <- results_distance_tree %>%
  summarise(
    name = '',
    max_MQ = round(max(MQ), 0),
    max_MDL = round(max(MDL), 0),
    max_CRG25 = round(max(`CRG-25`), 0),
    max_CRG50 = round(max(`CRG-50`), 0),
    max_CRG75 = round(max(`CRG-75`), 0)
  )

write_csv(results_distance_tree, RESULTS_DISTANCE_TREE)
write_csv(results_distance_tree_min, RESULTS_DISTANCE_TREE, na = "NA", append = TRUE)
write_csv(results_distance_tree_avg, RESULTS_DISTANCE_TREE, na = "NA", append = TRUE)
write_csv(results_distance_tree_std, RESULTS_DISTANCE_TREE, na = "NA", append = TRUE)
write_csv(results_distance_tree_max, RESULTS_DISTANCE_TREE, na = "NA", append = TRUE)

# Create long format for distance tree (no DEV column)
results_distance_tree_long <- results_distance_tree %>%
  pivot_longer(
    cols = c(MQ, MDL, `CRG-25`, `CRG-50`, `CRG-75`),
    names_to = "Algorithm",
    values_to = "Distance"
  ) %>%
  mutate(Algorithm = factor(Algorithm, levels = c("MQ", "MDL", "CRG-25", "CRG-50", "CRG-75")))

kruskal_distance <- kruskal.test(Distance ~ Algorithm, data = results_distance_tree_long)
print(kruskal_distance)

kruskal_results <- bind_rows(
  build_kruskal_row("number_of_packages", kruskal_packages),
  build_kruskal_row("number_of_edges", kruskal_edges),
  build_kruskal_row("avg_classes_per_package", kruskal_avgClasses),
  build_kruskal_row("single_package_commit_ratio", kruskal_commitRatio),
  build_kruskal_row("avg_package_commit", kruskal_avgPackageCommit),
  build_kruskal_row("distance_tree", kruskal_distance)
)

write_csv(kruskal_results, RESULTS_KRUSKAL_WALLIS)

distance_tree_heatmap_data <- all_data %>%
  mutate(
    projectName = acronyms[projectName]
  ) %>%
  select(
    projectName,
    mq_normalized_distance_mean,
    hmd_normalized_distance,
    crg_normalized_distance_25,
    crg_normalized_distance_50,
    crg_normalized_distance_75
  ) %>%
  rename(
    NAME = projectName,
    MQ = mq_normalized_distance_mean,
    MDL = hmd_normalized_distance,
    `CRG-25` = crg_normalized_distance_25,
    `CRG-50` = crg_normalized_distance_50,
    `CRG-75` = crg_normalized_distance_75
  ) %>%
  pivot_longer(
    cols = c(MQ, MDL, `CRG-25`, `CRG-50`, `CRG-75`),
    names_to = "Algorithm",
    values_to = "Distance"
  ) %>%
  mutate(
    Distance = round(Distance, 2)
  ) %>%
  mutate(
    min_distance = min(Distance, na.rm = TRUE),
    max_distance = max(Distance, na.rm = TRUE),
    relative_distance = if_else(
      max_distance > min_distance,
      (Distance - min_distance) / (max_distance - min_distance),
      0
    ),
    text_color = ifelse(relative_distance > 0.55, "white", "black")
  ) %>%
  mutate(
    Algorithm = factor(Algorithm, levels = c("MQ", "MDL", "CRG-25", "CRG-50", "CRG-75")),
    NAME = factor(NAME, levels = sort(unique(NAME), decreasing = TRUE))
  )

write_csv(
  distance_tree_heatmap_data %>% select(NAME, Algorithm, Distance) %>% mutate(Algorithm = as.character(Algorithm), NAME = as.character(NAME)) %>% pivot_wider(names_from = Algorithm, values_from = Distance),
  RESULTS_DISTANCE_TREE_HEATMAP
)

distance_tree_heatmap <- ggplot(distance_tree_heatmap_data, aes(x = Algorithm, y = NAME, fill = Distance)) +
  geom_tile(color = "white", linewidth = 0.5) +
  geom_text(aes(label = sprintf("%.2f", Distance), color = text_color), size = 4, fontface = "bold") +
  scale_color_identity() +
  scale_fill_gradient(low = "#F7F7F7", high = "#1A1A1A", trans = "sqrt", name = "Normalized Tree\nDistance to DEV") +
  scale_x_discrete(labels = c("MQ" = "MQ", "MDL" = "HMD", "CRG-25" = expression(HMD[25]^"+"), "CRG-50" = expression(HMD[50]^"+"), "CRG-75" = expression(HMD[75]^"+"))) +
  theme_classic() +
  theme(
    plot.background = element_rect(fill = "white", color = NA),
    panel.background = element_rect(fill = "white"),
    axis.title = element_blank(),
    axis.text.x = element_text(size = 12),
    axis.text.y = element_text(size = 11),
    axis.ticks = element_blank(),
    axis.line = element_blank(),
    legend.title = element_text(size = 10),
    legend.text = element_text(size = 9)
  )

ggsave(paste0(plots_dir, "distance_tree_heatmap.png"), distance_tree_heatmap, width = 7, height = 8, dpi = 300)
print(distance_tree_heatmap)

distance_tree_boxplot <- ggplot(
    results_distance_tree_long %>% mutate(Algorithm = factor(Algorithm, levels = c("MQ", "MDL", "CRG-25", "CRG-50", "CRG-75"))),
    aes(x = Algorithm, y = Distance, fill = Algorithm)
  ) +
  geom_boxplot(outlier.shape = 1, notch = FALSE, color = "black", staplewidth = 0.5) +
  scale_fill_manual(values = rep("white", 5)) +
  scale_x_discrete(labels = c("MQ" = "MQ", "MDL" = "HMD", "CRG-25" = expression(HMD[25]^'+'), "CRG-50" = expression(HMD[50]^'+'), "CRG-75" = expression(HMD[75]^'+'))) +
  labs(y = "Tree Distance to DEV") +
  theme_classic() +
  theme(
    plot.background = element_rect(fill = "white", color = NA),
    panel.background = element_rect(fill = "white"),
    legend.position = "none",
    axis.title.x = element_blank(),
    axis.title.y = element_text(size = 14),
    axis.text.x = element_text(size = 13),
    axis.text.y = element_text(size = 12),
    panel.grid.major.y = element_line(color = "gray90"),
    panel.grid.major.x = element_blank()
  )

ggsave(paste0(plots_dir, "distance_tree_boxplot.png"), distance_tree_boxplot, width = 10, height = 6, dpi = 300)
print(distance_tree_boxplot)

# Create boxplot for normalized tree distance to DEV
results_distance_tree_norm <- all_data %>%
  mutate(
    projectName = acronyms[projectName]
  ) %>%
  select(
    projectName,
    mq_normalized_distance_mean,
    hmd_normalized_distance,
    crg_normalized_distance_25,
    crg_normalized_distance_50,
    crg_normalized_distance_75
  ) %>%
  rename(
    NAME = projectName,
    MQ = mq_normalized_distance_mean,
    MDL = hmd_normalized_distance,
    `CRG-25` = crg_normalized_distance_25,
    `CRG-50` = crg_normalized_distance_50,
    `CRG-75` = crg_normalized_distance_75
  ) %>%
  arrange(NAME)

results_distance_tree_norm_long <- results_distance_tree_norm %>%
  pivot_longer(
    cols = c(MQ, MDL, `CRG-25`, `CRG-50`, `CRG-75`),
    names_to = "Algorithm",
    values_to = "Distance"
  ) %>%
  mutate(Algorithm = factor(Algorithm, levels = c("MQ", "MDL", "CRG-25", "CRG-50", "CRG-75")))

distance_tree_norm_boxplot <- ggplot(
    results_distance_tree_norm_long %>% mutate(Algorithm = factor(Algorithm, levels = c("MQ", "MDL", "CRG-25", "CRG-50", "CRG-75"))),
    aes(x = Algorithm, y = Distance, fill = Algorithm)
  ) +
  geom_boxplot(outlier.shape = 1, notch = FALSE, color = "black", staplewidth = 0.5) +
  scale_fill_manual(values = rep("white", 5)) +
  scale_x_discrete(labels = c("MQ" = "MQ", "MDL" = "HMD", "CRG-25" = expression(HMD[25]^'+'), "CRG-50" = expression(HMD[50]^'+'), "CRG-75" = expression(HMD[75]^'+'))) +
  labs(y = "Normalized Tree Distance to DEV") +
  theme_classic() +
  theme(
    plot.background = element_rect(fill = "white", color = NA),
    panel.background = element_rect(fill = "white"),
    legend.position = "none",
    axis.title.x = element_blank(),
    axis.title.y = element_text(size = 14),
    axis.text.x = element_text(size = 13),
    axis.text.y = element_text(size = 12),
    panel.grid.major.y = element_line(color = "gray90"),
    panel.grid.major.x = element_blank()
  )

ggsave(paste0(plots_dir, "distance_tree_boxplot_norm.png"), distance_tree_norm_boxplot, width = 10, height = 6, dpi = 300)
print(distance_tree_norm_boxplot)

# Create boxplot for movement ratio by class count (distance / number of classes)
results_movements_per_class <- all_data %>%
  mutate(
    projectName = acronyms[projectName],
    mq_movements_per_class = round(mq_distance_mean / original_numberOfNodes, 2),
    hmd_movements_per_class = round(hmd_distance / original_numberOfNodes, 2),
    crg25_movements_per_class = round(crg_distance_25 / original_numberOfNodes, 2),
    crg50_movements_per_class = round(crg_distance_50 / original_numberOfNodes, 2),
    crg75_movements_per_class = round(crg_distance_75 / original_numberOfNodes, 2)
  ) %>%
  select(
    projectName,
    mq_movements_per_class,
    hmd_movements_per_class,
    crg25_movements_per_class,
    crg50_movements_per_class,
    crg75_movements_per_class
  ) %>%
  rename(
    NAME = projectName,
    MQ = mq_movements_per_class,
    MDL = hmd_movements_per_class,
    `CRG-25` = crg25_movements_per_class,
    `CRG-50` = crg50_movements_per_class,
    `CRG-75` = crg75_movements_per_class
  ) %>%
  arrange(NAME)

results_movements_per_class_long <- results_movements_per_class %>%
  pivot_longer(
    cols = c(MQ, MDL, `CRG-25`, `CRG-50`, `CRG-75`),
    names_to = "Algorithm",
    values_to = "MovementsPerClass"
  ) %>%
  mutate(Algorithm = factor(Algorithm, levels = c("MQ", "MDL", "CRG-25", "CRG-50", "CRG-75")))

movements_per_class_boxplot <- ggplot(results_movements_per_class_long, aes(x = Algorithm, y = MovementsPerClass, fill = Algorithm)) +
  geom_boxplot(outlier.shape = 1, notch = FALSE, color = "black", staplewidth = 0.5) +
  scale_fill_manual(values = rep("white", 5)) +
  scale_x_discrete(labels = c("MQ" = "MQ", "MDL" = "HMD", "CRG-25" = expression(HMD[25]^'+'), "CRG-50" = expression(HMD[50]^'+'), "CRG-75" = expression(HMD[75]^'+'))) +
  labs(y = "Movements / Number of Classes") +
  theme_classic() +
  theme(
    plot.background = element_rect(fill = "white", color = NA),
    panel.background = element_rect(fill = "white"),
    legend.position = "none",
    axis.title.x = element_blank(),
    axis.title.y = element_text(size = 14),
    axis.text.x = element_text(size = 13),
    axis.text.y = element_text(size = 12),
    panel.grid.major.y = element_line(color = "gray90"),
    panel.grid.major.x = element_blank()
  )

results_movements_per_class_min <- results_movements_per_class %>%
  summarise(NAME = "Min", MQ = min(MQ, na.rm = TRUE), MDL = min(MDL, na.rm = TRUE), `CRG-25` = min(`CRG-25`, na.rm = TRUE), `CRG-50` = min(`CRG-50`, na.rm = TRUE), `CRG-75` = min(`CRG-75`, na.rm = TRUE))

results_movements_per_class_avg <- results_movements_per_class %>%
  summarise(NAME = "Mean", MQ = round(mean(MQ, na.rm = TRUE), 2), MDL = round(mean(MDL, na.rm = TRUE), 2), `CRG-25` = round(mean(`CRG-25`, na.rm = TRUE), 2), `CRG-50` = round(mean(`CRG-50`, na.rm = TRUE), 2), `CRG-75` = round(mean(`CRG-75`, na.rm = TRUE), 2))

results_movements_per_class_std <- results_movements_per_class %>%
  summarise(NAME = "Std. Dev.", MQ = round(sd(MQ, na.rm = TRUE), 2), MDL = round(sd(MDL, na.rm = TRUE), 2), `CRG-25` = round(sd(`CRG-25`, na.rm = TRUE), 2), `CRG-50` = round(sd(`CRG-50`, na.rm = TRUE), 2), `CRG-75` = round(sd(`CRG-75`, na.rm = TRUE), 2))

results_movements_per_class_max <- results_movements_per_class %>%
  summarise(NAME = "Max", MQ = max(MQ, na.rm = TRUE), MDL = max(MDL, na.rm = TRUE), `CRG-25` = max(`CRG-25`, na.rm = TRUE), `CRG-50` = max(`CRG-50`, na.rm = TRUE), `CRG-75` = max(`CRG-75`, na.rm = TRUE))

movements_per_class_heatmap_data <- results_movements_per_class_long %>%
  mutate(
    Algorithm = factor(Algorithm, levels = c("MQ", "MDL", "CRG-25", "CRG-50", "CRG-75")),
    NAME = factor(NAME, levels = sort(unique(NAME), decreasing = TRUE)),
    min_movements = min(MovementsPerClass, na.rm = TRUE),
    max_movements = max(MovementsPerClass, na.rm = TRUE),
    relative_movements = if_else(
      max_movements > min_movements,
      (MovementsPerClass - min_movements) / (max_movements - min_movements),
      0
    ),
    text_color = ifelse(relative_movements > 0.55, "white", "black")
  )

movements_per_class_heatmap <- ggplot(movements_per_class_heatmap_data, aes(x = Algorithm, y = NAME, fill = MovementsPerClass)) +
  geom_tile(color = "white", linewidth = 0.5) +
  geom_text(aes(label = sprintf("%.2f", MovementsPerClass), color = text_color), size = 3.5, fontface = "bold") +
  scale_color_identity() +
  scale_fill_gradient(low = "#F7F7F7", high = "#1A1A1A", trans = "sqrt", name = "Movements /\nClass") +
  scale_x_discrete(labels = c("MQ" = "MQ", "MDL" = "HMD", "CRG-25" = expression(HMD[25]^"+"), "CRG-50" = expression(HMD[50]^"+"), "CRG-75" = expression(HMD[75]^"+"))) +
  theme_classic() +
  theme(
    plot.background = element_rect(fill = "white", color = NA),
    panel.background = element_rect(fill = "white"),
    axis.title = element_blank(),
    axis.text.x = element_text(size = 12),
    axis.text.y = element_text(size = 11),
    axis.ticks = element_blank(),
    axis.line = element_blank(),
    legend.title = element_text(size = 10),
    legend.text = element_text(size = 9)
  )

ggsave(paste0(plots_dir, "movements_per_class_boxplot.png"), movements_per_class_boxplot, width = 10, height = 6, dpi = 300)
print(movements_per_class_boxplot)

ggsave(paste0(plots_dir, "movements_per_class_heatmap.png"), movements_per_class_heatmap, width = 7, height = 8, dpi = 300)
print(movements_per_class_heatmap)

write_csv(results_movements_per_class, RESULTS_MOVEMENTS_PER_CLASS)
write_csv(results_movements_per_class_min, RESULTS_MOVEMENTS_PER_CLASS, na = "NA", append = TRUE)
write_csv(results_movements_per_class_avg, RESULTS_MOVEMENTS_PER_CLASS, na = "NA", append = TRUE)
write_csv(results_movements_per_class_std, RESULTS_MOVEMENTS_PER_CLASS, na = "NA", append = TRUE)
write_csv(results_movements_per_class_max, RESULTS_MOVEMENTS_PER_CLASS, na = "NA", append = TRUE)


# ------------------ Metrics --------------------------

fitered_data <- original_summary %>%
  filter(!projectName %in% eliminated_projects) %>%
  inner_join(mq_summary, by = "projectName") %>% 
  inner_join(hmd_summary, by = "projectName") %>% 
  inner_join(crg_summary, by = "projectName")

fitered_data <- fitered_data %>%
  mutate(
    projectName = acronyms[projectName]
  )

# Function to create comparison plots for a specific metric
create_metric_comparison_plot <- function(data, metric_name, y_label, title_suffix = "") {
  # Define the columns for each algorithm
  original_col <- paste0("original_", metric_name)
  mq_col <- paste0("mq_", metric_name, "_mean")
  hmd_col <- paste0("hmd_", metric_name)
  crg_25_col <- paste0("crg_", metric_name, "_25")
  crg_50_col <- paste0("crg_", metric_name, "_50")
  crg_75_col <- paste0("crg_", metric_name, "_75")
  
  # Check which columns exist in the data
  available_cols <- c(original_col, mq_col, hmd_col, crg_25_col, crg_50_col, crg_75_col)
  existing_cols <- available_cols[available_cols %in% names(data)]
  
  if (length(existing_cols) < 2) {
    warning(paste("Not enough columns found for metric:", metric_name))
    return(NULL)
  }
  
  # Prepare data for plotting
  plot_data <- data %>%
    select(projectName, all_of(existing_cols)) %>%
    pivot_longer(
      cols = -projectName,
      names_to = "algorithm",
      values_to = "value"
    ) %>%
    filter(!is.na(value)) %>%
    mutate(
      algorithm_clean = case_when(
        str_starts(algorithm, "original_") ~ "DEV",
        str_starts(algorithm, "mq_") ~ "MQ",
        str_starts(algorithm, "hmd_") ~ "MDL",
        str_starts(algorithm, "crg_") & str_ends(algorithm, "_25") ~ "CRG-25",
        str_starts(algorithm, "crg_") & str_ends(algorithm, "_50") ~ "CRG-50",
        str_starts(algorithm, "crg_") & str_ends(algorithm, "_75") ~ "CRG-75",
        TRUE ~ algorithm
      ),
      algorithm_clean = factor(algorithm_clean, levels = c("DEV", "MQ", "MDL", "CRG-25", "CRG-50", "CRG-75"))
    )
  
  # Create the plot
  p <- ggplot(plot_data, aes(x = projectName, y = value, fill = algorithm_clean)) +
    geom_col(position = "dodge", alpha = 0.8) +
    scale_fill_viridis_d(name = "Algorithm", option = "plasma", labels = c("DEV" = "DEV", "MQ" = "MQ", "MDL" = "HMD", "CRG-25" = expression(HMD[25]^'+'), "CRG-50" = expression(HMD[50]^'+'), "CRG-75" = expression(HMD[75]^'+'))) +
    labs(
      title = paste("Comparison of", str_to_title(str_replace_all(metric_name, "_", " ")), "Across Algorithms", title_suffix),
      x = "Project",
      y = y_label
    ) +
    theme_minimal() +
    theme(
      axis.text.x = element_text(angle = 45, hjust = 1, size = 8),
      legend.position = "bottom",
      plot.title = element_text(size = 12, face = "bold"),
      panel.grid.minor = element_blank()
    ) +
    guides(fill = guide_legend(nrow = 2))
  
  return(p)
}

#
# ANALISE ESTATISTICA DOS RESULTADOS
#
normalized_algorithm_lookup <- c(
  mq_normalized_distance_mean = "MQ",
  hmd_normalized_distance = "MDL",
  crg_normalized_distance_25 = "CRG-25",
  crg_normalized_distance_50 = "CRG-50",
  crg_normalized_distance_75 = "CRG-75"
)

normalized_zssn_data <- all_data %>%
  select(
    projectName,
    mq_normalized_distance_mean,
    hmd_normalized_distance,
    crg_normalized_distance_25,
    crg_normalized_distance_50,
    crg_normalized_distance_75
  ) %>%
  drop_na()

normalized_zssn_long <- normalized_zssn_data %>%
  pivot_longer(
    cols = -projectName,
    names_to = "Algorithm",
    values_to = "NormalizedDistance"
  ) %>%
  mutate(
    Algorithm = factor(
      Algorithm,
      levels = names(normalized_algorithm_lookup),
      labels = unname(normalized_algorithm_lookup)
    )
  )

normalized_friedman <- friedman.test(NormalizedDistance ~ Algorithm | projectName, data = normalized_zssn_long)
print(normalized_friedman)

normalized_pairwise_results <- bind_rows(lapply(combn(names(normalized_algorithm_lookup), 2, simplify = FALSE), function(pair) {
  left_name <- pair[[1]]
  right_name <- pair[[2]]

  pair_data <- normalized_zssn_data %>%
    select(projectName, all_of(c(left_name, right_name))) %>%
    drop_na()

  left_values <- pair_data[[left_name]]
  right_values <- pair_data[[right_name]]
  pair_test <- wilcox.test(left_values, right_values, paired = TRUE, exact = FALSE, correct = FALSE)

  tibble(
    comparison = paste(normalized_algorithm_lookup[[left_name]], "vs", normalized_algorithm_lookup[[right_name]]),
    n = nrow(pair_data),
    statistic = round(as.numeric(unname(pair_test$statistic)), 3),
    p_value = round(pair_test$p.value, 5),
    median_difference = round(median(left_values - right_values), 5),
    effect_size_rank_biserial = paired_rank_biserial(left_values, right_values)
  )
})) %>%
  mutate(
    p_adjusted = round(p.adjust(p_value, method = "holm"), 5),
    significant_0_05 = p_adjusted < 0.05
  )

print(normalized_pairwise_results)

write_csv(
  tibble(
    metric = "normalized_zssn_distance",
    method = normalized_friedman$method,
    statistic = round(as.numeric(unname(normalized_friedman$statistic)), 3),
    parameter = round(as.numeric(unname(normalized_friedman$parameter)), 3),
    p_value = round(normalized_friedman$p.value, 5),
    significant_0_05 = normalized_friedman$p.value < 0.05
  ),
  RESULTS_NORMALIZED_TESTS
)

write_csv(normalized_pairwise_results, RESULTS_NORMALIZED_PAIRWISE)

if (normalized_friedman$p.value <= 0.05) {
  print("Normalized ZSS/N Friedman test is significant; inspect pairwise results for the differing algorithms.")
} else {
  print("Normalized ZSS/N Friedman test is not significant at alpha = 0.05.")
}


#
# ANALISE DE CORRELACAO ENTRE MQ E OS OUTROS ALGORITMOS
#
correlation_data <- zss_structural_metrics %>% 
  mutate(algorithm = if_else(algorithm == "crg", paste0("crg", variant), algorithm)) %>% 
  group_by(projectName, algorithm) %>% 
  summarize(distance = median(distance), .groups = "drop") %>% 
  pivot_wider(names_from = algorithm, values_from = distance) %>% 
  mutate(diff_crg25_mq = crg25 - mq) %>% 
  mutate(diff_crg50_mq = crg50 - mq) %>% 
  mutate(diff_crg75_mq = crg75 - mq) %>% 
  mutate(diff_hmd_mq = hmd - mq) %>% 
  select(projectName, diff_crg25_mq, diff_crg50_mq, diff_crg75_mq, diff_hmd_mq)
  
correlation_data <- correlation_data %>% 
  inner_join(original_summary, by="projectName") %>% 
  select(projectName, diff_crg25_mq, diff_crg50_mq, diff_crg75_mq, diff_hmd_mq, classes=original_numberOfNodes, deps=original_numberOfEdges, spcommits=original_singlePackageCommitRatio, avgcommits=original_avgPackageCommit)

#
# Correlacao da diferenca entre CRG-25 e MQ
#

# CRG25-MQ x Classes: Sem correlacao: -0.0014
cor(correlation_data$diff_crg25_mq, correlation_data$classes)

# CRG25-MQ x Dependencias: Correlacao fraca: -0.3961
cor(correlation_data$diff_crg25_mq, correlation_data$deps)

# CRG25-MQ x Single Package Commits: Correlacao fraca: 0.0917
cor(correlation_data$diff_crg25_mq, correlation_data$spcommits)

# CRG25-MQ x Avg Packages Commit: Correlacao fraca: -0.0708
cor(correlation_data$diff_crg25_mq, correlation_data$avgcommits)

#
# Correlacao da diferenca entre CRG-50 e MQ
#

# CRG50-MQ x Classes: Correlacao fraca: -0.25
cor(correlation_data$diff_crg50_mq, correlation_data$classes)

# CRG50-MQ x Dependencias: Correlacao média: -0.5571
cor(correlation_data$diff_crg50_mq, correlation_data$deps)

# CRG50-MQ x Single Package Commits: Correlacao fraca: 0.1048
cor(correlation_data$diff_crg50_mq, correlation_data$spcommits)

# CRG50-MQ x Avg Packages Commit: Correlacao fraca: -0.0023
cor(correlation_data$diff_crg50_mq, correlation_data$avgcommits)

#
# Correlacao da diferenca entre CRG-75 e MQ
#

# CRG75-MQ x Classes: Correlacao média: -0.59
cor(correlation_data$diff_crg75_mq, correlation_data$classes)

# CRG75-MQ x Dependencias: Correlacao média: -0.6479
cor(correlation_data$diff_crg75_mq, correlation_data$deps)

# CRG75-MQ x Single Package Commits: Correlacao fraca: 0.1723
cor(correlation_data$diff_crg75_mq, correlation_data$spcommits)

# CRG75-MQ x Avg Packages Commit: Correlacao fraca: -0.1346
cor(correlation_data$diff_crg75_mq, correlation_data$avgcommits)

#
# Correlacao da diferenca entre MDL e MQ
#

# MDL-MQ x Classes: Correlacao forte: -0.8685
cor(correlation_data$diff_hmd_mq, correlation_data$classes)

# MDL-MQ x Dependencias: Correlacao forte: -0.8679
cor(correlation_data$diff_hmd_mq, correlation_data$deps)

# MDL-MQ x Single Package Commits: Correlacao fraca: 0.1606
cor(correlation_data$diff_hmd_mq, correlation_data$spcommits)

# MDL-MQ x Avg Packages Commit: Correlacao fraca: -0.1806
cor(correlation_data$diff_hmd_mq, correlation_data$avgcommits)


#
# ANALISE DE CORRELACAO ENTRE OS ALGORITMOS EX-MQ
#
correlation_data <- zss_structural_metrics %>% 
  mutate(algorithm = if_else(algorithm == "crg", paste0("crg", variant), algorithm)) %>% 
  group_by(projectName, algorithm) %>% 
  summarize(distance = median(distance), .groups = "drop") %>% 
  pivot_wider(names_from = algorithm, values_from = distance) %>% 
  mutate(diff_crg25_hmd = crg25 - hmd) %>% 
  mutate(diff_crg50_crg25 = crg50 - crg25) %>% 
  mutate(diff_crg75_crg50 = crg75 - crg50) %>% 
  select(projectName, diff_crg25_hmd, diff_crg50_crg25, diff_crg75_crg50)

correlation_data <- correlation_data %>% 
  inner_join(original_summary, by="projectName") %>% 
  select(projectName, diff_crg25_hmd, diff_crg50_crg25, diff_crg75_crg50, classes=original_numberOfNodes, deps=original_numberOfEdges, spcommits=original_singlePackageCommitRatio, avgcommits=original_avgPackageCommit)

#
# Correlacao da diferenca entre CRG-25 e MDL
#

# CRG25-MDL x Classes: Correlacao fraca: 0.32767
cor(correlation_data$diff_crg25_hmd, correlation_data$classes)

# CRG25-MDL x Dependencias: Sem correlacao: -0.0750
cor(correlation_data$diff_crg25_hmd, correlation_data$deps)

# CRG25-MDL x Single Package Commits: Sem correlacao: 0.0324
cor(correlation_data$diff_crg25_hmd, correlation_data$spcommits)

# CRG25-MDL x Avg Packages Commit: Sem correlacao: -0.0039
cor(correlation_data$diff_crg25_hmd, correlation_data$avgcommits)

#
# Correlacao da diferenca entre CRG-50 e CRG-25
#

# CRG50-CRG25 x Classes: Correlacao fraca: -0.2923
cor(correlation_data$diff_crg50_crg25, correlation_data$classes)

# CRG50-CRG25 x Dependencias: Sem correlacao: -0.0997
cor(correlation_data$diff_crg50_crg25, correlation_data$deps)

# CRG50-CRG25 x Single Package Commits: Sem correlacao: -0.0026
cor(correlation_data$diff_crg50_crg25, correlation_data$spcommits)

# CRG50-CRG25 x Avg Packages Commit: Sem correlacao: 0.0659
cor(correlation_data$diff_crg50_crg25, correlation_data$avgcommits)

#
# Correlacao da diferenca entre CRG-75 e CRG-50
#

# CRG75-CRG50 x Classes: Correlacao fraca: -0.2446
cor(correlation_data$diff_crg75_crg50, correlation_data$classes)

# CRG75-CRG50 x Dependencias: Sem correlacao: 0.0768
cor(correlation_data$diff_crg75_crg50, correlation_data$deps)

# CRG75-CRG50 x Single Package Commits: Sem correlacao: 0.0323
cor(correlation_data$diff_crg75_crg50, correlation_data$spcommits)

# CRG75-CRG50 x Avg Packages Commit: Sem correlacao: -0.0981
cor(correlation_data$diff_crg75_crg50, correlation_data$avgcommits)
