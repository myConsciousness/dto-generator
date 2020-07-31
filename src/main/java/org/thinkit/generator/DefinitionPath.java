
/**
 * Project Name : Generator<br>
 * File Name : DefinitionPath.java<br>
 * Encoding : UTF-8<br>
 * Creation Date : 2020/06/15<br>
 * <p>
 * Copyright © 2020 Kato Shinya. All rights reserved.
 * <p>
 * This source code or any portion thereof must not be<br>
 * reproduced or used in any manner whatsoever.
 */

package org.thinkit.generator;

import com.google.common.flogger.FluentLogger;

import org.apache.commons.lang3.StringUtils;
import org.thinkit.common.catalog.Platform;
import org.thinkit.common.command.CommandInvoker;
import org.thinkit.common.util.file.FluentFile;
import org.thinkit.generator.command.DefaultOutputPathCollector;
import org.thinkit.generator.vo.DefaultOutputPath;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/**
 * 定義書のファイルパスと出力先パスを管理するクラスです。
 * <p>
 * 以下の静的メソッドを呼び出すことで {@link DefinitionPath} のインスタンスを生成することができます。
 * <p>
 * {@link #of(String)} <br>
 * {@link #of(String, String)} <br>
 * <p>
 * {@link DefinitionPath} のインスタンス生成時に設定されたファイルパスは<br>
 * {@link #getFilePath()} を呼び出すことで取得できます。
 * <p>
 * {@link DefinitionPath} のインスタンス生成時に出力先パスの設定は必須ではありませんが、<br>
 * 出力先パスが設定された場合は {@link #getOutputPath()} の呼び出し時には設定された値が優先的に返却されます。
 * <p>
 * {@link DefinitionPath} のインスタンス生成時に出力先パスが設定されない状態で {@link #getOutputPath()}
 * が呼び出された場合は、<br>
 * {@link DefaultOutputPathManager#execute()}
 * を呼び出しプラットフォームに対応した既定出力先のパスを生成し返却します。<br>
 * この既定出力先のパスを生成する際にエラーが発生した場合は {@link #getOutputPath()} は必ず空文字列を返却します。
 *
 * @author Kato Shinya
 * @since 1.0
 * @version 1.0
 *
 * @see #getFilePath()
 * @see #getOutputPath()
 */
@ToString
@EqualsAndHashCode
public final class DefinitionPath {

    /**
     * ログ出力オブジェクト
     */
    private static final FluentLogger logger = FluentLogger.forEnclosingClass();

    /**
     * ファイルパス
     */
    @Getter
    private String filePath = "";

    /**
     * 出力先パス
     */
    private String outputPath = "";

    /**
     * デフォルトコンストラクタ
     */
    private DefinitionPath() {
    }

    /**
     * コンストラクタ
     *
     * @param filePath 生成する情報が定義されたファイルへのパス
     *
     * @exception NullPointerException 引数として {@code null} が渡された場合
     * @throws IllegalArgumentException ファイルパスが空文字列の場合
     */
    private DefinitionPath(@NonNull String filePath) {
        this(filePath, "");
    }

    /**
     * コンストラクタ
     *
     * @param filePath   生成する情報が定義されたファイルへのパス(必須)
     * @param outputParh 生成された情報の出力先（任意）
     *
     * @exception NullPointerException 引数として {@code null} が渡された場合
     * @throws IllegalArgumentException ファイルパスが空文字列の場合
     */
    private DefinitionPath(@NonNull String filePath, @NonNull String outputPath) {

        if (StringUtils.isBlank(filePath)) {
            logger.atSevere().log("処理を実行するためには有効なファイルパスを指定する必要があります。");
            throw new IllegalArgumentException("File path is required.");
        }

        this.filePath = filePath;

        if (StringUtils.isEmpty(outputPath)) {
            this.outputPath = this.getDefaultOutputPath();
        } else {
            this.outputPath = outputPath;
        }
    }

    /**
     * コンストラクタ
     *
     * @param filePath 生成する情報が定義されたファイルへのパス
     *
     * @exception NullPointerException 引数として {@code null} が渡された場合
     * @throws IllegalArgumentException ファイルパスが空文字列の場合
     */
    public static DefinitionPath of(@NonNull String filePath) {
        return new DefinitionPath(filePath, "");
    }

    /**
     * コンストラクタ
     *
     * @param filePath   生成する情報が定義されたファイルへのパス(必須)
     * @param outputParh 生成された情報の出力先（任意）
     *
     * @exception NullPointerException 引数として {@code null} が渡された場合
     * @throws IllegalArgumentException ファイルパスが空文字列の場合
     */
    public static DefinitionPath of(@NonNull String filePath, @NonNull String outputPath) {
        return new DefinitionPath(filePath, outputPath);
    }

    /**
     * 出力先のパスを返却します。 <br>
     * {@link DefinitionPath} のインスタンス生成時に出力先パスをコンストラクタへ渡した場合は、<br>
     * インスタンス生成時の出力先パスが優先的に返却されます。
     * <p>
     * {@link DefinitionPath} のインスタンス生成時に出力パスを指定しなかった場合、または空文字列を指定した場合は、<br>
     * プログラム実行時のプラットフォームに応じた既定の出力先パスを取得し返却します。
     * <p>
     * 以下の場合は空文字列を返却します。<br>
     * 1, {@link Platform#getPlatform()} 実行時に未対応のプラットフォームでプログラムが実行されたことを検知された場合<br>
     * 2, {@link DefaultOutputPathManager#execute()} 実行時にエラーが発生した場合
     *
     * @return 出力先のパス
     */
    public String getOutputPath() {
        logger.atInfo().log("出力先のパス = (%s)", this.outputPath);
        return this.outputPath;
    }

    /**
     * 既定の出力先パスを取得し返却します。<br>
     * 既定の出力パスを取得する際にエラーが発生した場合は空文字列を返却します。
     *
     * @return 既定の出力先パス
     */
    private String getDefaultOutputPath() {

        final Platform platform = Platform.getPlatform();
        logger.atInfo().log("プログラム実行時のプラットフォーム = (%s)", platform);

        if (platform == null) {
            logger.atSevere().log("未対応のプラットフォームでプログラムが実行されました。");
            return "";
        }

        final DefaultOutputPath defaultOutputPath = CommandInvoker.of(DefaultOutputPathCollector.of(platform)).invoke();

        final StringBuilder outputPath = new StringBuilder();
        outputPath.append(System.getenv(defaultOutputPath.getEnvironmentVariableName()))
                .append(FluentFile.getFileSeparator()).append(defaultOutputPath.getOutputDirectory());

        return outputPath.toString();
    }
}