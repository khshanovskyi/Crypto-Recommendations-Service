package epam.com.khshanovskyi.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import epam.com.khshanovskyi.exception.UnableToFindFolderByPathException;

class FilesUtilTest {

    private static final String WRONG_PATH = "wrong.path";
    private static final String PATH_TO_FOLDER_WITH_FOLDERS = "src/test/resources/crypto";
    private static final String PATH_TO_FOLDER_WITH_FILES = "src/test/resources/crypto/2021-11";

    @ParameterizedTest
    @NullSource
    @EmptySource
    @DisplayName("getFoldersPath -> throws NullPointerException when param is null or empty")
    void getFoldersPath_ThrowsNullPointerExceptionWhenParamNullOrEmpty(String pathToBaseFolder) {
        assertThrows(NullPointerException.class, () -> FilesUtil.getFoldersPath(pathToBaseFolder));
    }

    @Test
    @DisplayName("getFoldersPath -> throws UnableToFindFolderByPathException when param is incorrect")
    void getFoldersPath_ThrowsUnableToFindFolderByPathException() {
        assertThrows(UnableToFindFolderByPathException.class, () -> FilesUtil.getFoldersPath(WRONG_PATH));
    }

    @Test
    @DisplayName("getFoldersPath -> check if result is correct")
    void getFoldersPath_ProvidesFolderName() {
        List<String> foldersPath = FilesUtil.getFoldersPath(PATH_TO_FOLDER_WITH_FOLDERS);
        assertTrue(foldersPath.contains("src\\test\\resources\\crypto\\2022-01"));
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @DisplayName("getFolderPathOfTheLastPresentedMonth -> throws NullPointerException when param is null or empty")
    void getFolderPathOfTheLastPresentedMonth_ThrowsNullPointerExceptionWhenParamNullOrEmpty(String pathToBaseFolder) {
        assertThrows(NullPointerException.class,
          () -> FilesUtil.getFolderPathOfTheLastPresentedMonth(pathToBaseFolder));
    }

    @ParameterizedTest
    @ValueSource(strings = {WRONG_PATH, PATH_TO_FOLDER_WITH_FILES})
    @DisplayName("getFolderPathOfTheLastPresentedMonth -> throws UnableToFindFolderByPathException when param is incorrect")
    void getFolderPathOfTheLastPresentedMonth_ThrowsUnableToFindFolderByPathException(String pathToBaseFolder) {
        assertThrows(UnableToFindFolderByPathException.class,
          () -> FilesUtil.getFolderPathOfTheLastPresentedMonth(pathToBaseFolder));
    }

    @Test
    @DisplayName("getFolderPathOfTheLastPresentedMonth -> check if result is correct")
    void getFolderPathOfTheLastPresentedMonth_ProvidesFolderName() {
        assertEquals("src\\test\\resources\\crypto\\2022-01",
          FilesUtil.getFolderPathOfTheLastPresentedMonth(PATH_TO_FOLDER_WITH_FOLDERS));
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @DisplayName("getFileNamesWithPath -> throws NullPointerException when param is null or empty")
    void getFileNamesWithPath_ThrowsNullPointerExceptionWhenParamNullOrEmpty(String pathToBaseFolder) {
        assertThrows(NullPointerException.class, () -> FilesUtil.getFileNamesWithPath(pathToBaseFolder));
    }

    @Test
    @DisplayName("getFileNamesWithPath -> throws UnableToFindFolderByPathException when param is incorrect")
    void getFileNamesWithPath_ThrowsUnableToFindFolderByPathException() {
        assertThrows(UnableToFindFolderByPathException.class, () -> FilesUtil.getFileNamesWithPath(WRONG_PATH));
    }

    @Test
    @DisplayName("getFileNamesWithPath -> check if result is correct")
    void getFileNamesWithPath_ProvidesFileNames() {
        List<String> fileNamesWithPath = FilesUtil.getFileNamesWithPath(PATH_TO_FOLDER_WITH_FILES);
        assertTrue(fileNamesWithPath.contains("src\\test\\resources\\crypto\\2021-11\\ETH_values.csv"));
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @DisplayName("getCryptoNamesFromFileNames -> throws NullPointerException when param is null or empty")
    void getCryptoNamesFromFileNames_ThrowsNullPointerExceptionWhenParamNullOrEmpty(String pathToBaseFolder) {
        assertThrows(NullPointerException.class, () -> FilesUtil.getCryptoNamesFromFileNames(pathToBaseFolder));
    }

    @Test
    @DisplayName("getCryptoNamesFromFileNames -> throws UnableToFindFolderByPathException when param is incorrect")
    void getCryptoNamesFromFileNames_ThrowsUnableToFindFolderByPathException() {
        assertThrows(UnableToFindFolderByPathException.class, () -> FilesUtil.getCryptoNamesFromFileNames(WRONG_PATH));
    }

    @Test
    @DisplayName("getCryptoNamesFromFileNames -> check if result is correct")
    void getCryptoNamesFromFileNames_ProvidesFileNames() {
        List<String> cryptoNames = FilesUtil.getCryptoNamesFromFileNames(PATH_TO_FOLDER_WITH_FILES);
        assertTrue(cryptoNames.contains("ETH"));
        assertTrue(cryptoNames.contains("LTC"));
    }

}