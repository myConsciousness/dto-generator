/*
 * Copyright 2020 Kato Shinya.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.thinkit.generator;

import org.thinkit.common.catalog.Extension;
import org.thinkit.common.util.file.FluentFile;
import org.thinkit.generator.common.Generator;
import org.thinkit.generator.content.dto.rule.DtoResourceFacade;
import org.thinkit.generator.workbook.common.AbstractGenerator;
import org.thinkit.generator.workbook.common.DefinitionPath;

import lombok.NonNull;

/**
 * DTO定義書を解析してDTOクラスを生成する処理を定義したクラスです。
 *
 * @author Kato Shinya
 * @since 1.0
 * @version 1.0
 */
final class DtoGenerator extends AbstractGenerator {

    /**
     * コンストラクタ
     *
     * @param definitionPath 定義書のパス
     *
     * @exception NullPointerException 引数として {@code null} が渡された場合
     */
    private DtoGenerator(@NonNull DefinitionPath definitionPath) {
        super(definitionPath);
    }

    /**
     * 引数として渡された {@code definitionPath} を基に {@link DtoGenerator}
     * クラスの新しいインスタンスを生成し返却します。
     *
     * @param definitionPath 定義書のパス
     * @return {@link DtoGenerator} クラスの新しいインスタンス
     *
     * @exception NullPointerException 引数として {@code null} が渡された場合
     */
    public static Generator of(@NonNull DefinitionPath definitionPath) {
        return new DtoGenerator(definitionPath);
    }

    @Override
    protected boolean run() {

        DtoResourceFacade.createResource(super.getFilePath()).forEach(dtoResource -> {
            FluentFile.writerOf(super.getOutputPath(dtoResource.getPackageName())).write(dtoResource.getResourceName(),
                    Extension.java(), dtoResource.getResource());
        });

        return true;
    }
}
