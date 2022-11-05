package epam.com.khshanovskyi.util;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import epam.com.khshanovskyi.exception.UnableToFindFolderByPathException;
import lombok.extern.slf4j.Slf4j;

/**
 * Utils for working with files.
 */
@Slf4j
public final class FilesUtil {

    private FilesUtil() {
    }

    /**
     * Extract all folder names with files in reversed order:
     * crypto -> 2022-01
     *           2021-12
     *           2021-11
     *
     * @param pathToBaseFolder path to folder with folders that contains Crypto info files
     * @return {@link String}
     * @throws UnableToFindFolderByPathException in case if folder is not present
     */
    public static List<String> getFoldersPath(String pathToBaseFolder) {
        return getStreamOfDirectoryNames(pathToBaseFolder)
          .sorted(Comparator.reverseOrder())
          .toList();
    }

    /**
     * Extract folder name of the last presented month.
     *
     * @param pathToBaseFolder path to folder with folders that contains Crypto info files
     * @return {@link String}
     * @throws UnableToFindFolderByPathException in case if folder is not present
     */
    public static String getFolderPathOfTheLastPresentedMonth(String pathToBaseFolder) {
        return getStreamOfDirectoryNames(pathToBaseFolder)
          .max(Comparator.naturalOrder())
          .orElseThrow(generateUnableToFindFolderByPathException(
            String.format("Passed path with base folder [%s] going to file, not to folder!", pathToBaseFolder)));
    }

    /**
     * Extract all file names with current root path from the specified folder.
     *
     * @param pathToFolder path to folder that contains Crypto info files
     * @return {@link List<String>} with file names
     * @throws UnableToFindFolderByPathException in case if folder is not present
     */
    public static List<String> getFileNamesWithPath(String pathToFolder) {
        checkIfEmpty(pathToFolder);

        return Arrays.stream(getListFiles(pathToFolder))
          .filter(File::isFile)
          .map(File::getPath)
          .toList();
    }


    /**
     * Extract all file names with current root path from the specified folder.
     *
     * @param pathToFolder path to folder that contains Crypto info files
     * @return {@link List<String>} with short crypto name (Bitcoin -> BTC)
     * @throws UnableToFindFolderByPathException in case if folder is not present
     */
    public static List<String> getCryptoNamesFromFileNames(String pathToFolder) {
        checkIfEmpty(pathToFolder);

        return Arrays.stream(getListFiles(pathToFolder))
          .filter(File::isFile)
          .map(File::getName)
          .map(fileName -> fileName.split("_")[0])
          .toList();
    }

    private static void checkIfEmpty(String pathToFolder) {
        if (StringUtils.isEmpty(pathToFolder)) {
            log.warn("Provided path to folder is null or empty");
            throw new NullPointerException("Provided path to folder cannot be null or empty");
        }
    }

    private static Stream<String> getStreamOfDirectoryNames(String pathToBaseFolder) {
        checkIfEmpty(pathToBaseFolder);

        return Arrays.stream(getListFiles(pathToBaseFolder))
          .filter(File::isDirectory)
          .map(File::getPath);
    }

    private static File[] getListFiles(String pathToFolder) {
        return Optional.ofNullable(new File(pathToFolder).listFiles())
          .orElseThrow(generateUnableToFindFolderByPathException(String.format(
            "Unable to find folder with crypto files by the path [%s]. Please take a look if the " +
              "folder with provided name is exists.", pathToFolder)));
    }

    private static Supplier<UnableToFindFolderByPathException> generateUnableToFindFolderByPathException(
      String message) {
        return () -> new UnableToFindFolderByPathException(message);
    }

}
