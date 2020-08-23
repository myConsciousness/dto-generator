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

package org.thinkit.generator.content.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.thinkit.framework.content.Attribute;
import org.thinkit.framework.content.Condition;
import org.thinkit.framework.content.Content;
import org.thinkit.framework.content.annotation.ContentMapping;
import org.thinkit.generator.content.dto.entity.DtoCreatorItem;
import org.thinkit.generator.content.dto.entity.DtoCreatorItemGroup;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * コンテンツ「DTO作成者項目」の情報をロードするコンテンツクラスです。
 *
 * @author Kato Shinya
 * @since 1.0
 * @version 1.0
 */
@ToString
@EqualsAndHashCode
@ContentMapping(content = "dto/DtoCreatorItem")
public final class DtoCreatorItemLoader implements Content<DtoCreatorItemGroup> {

    /**
     * デフォルトコンストラクタ
     */
    private DtoCreatorItemLoader() {
    }

    /**
     * {@link DtoCreatorItemLoader} クラスの新しいインスタンスを生成し返却します。
     *
     * @return {@link DtoCreatorItemLoader} クラスの新しいインスタンス
     */
    public static Content<DtoCreatorItemGroup> of() {
        return new DtoCreatorItemLoader();
    }

    /**
     * コンテンツ要素定数
     */
    private enum ContentAttribute implements Attribute {
        セル項目コード, セル項目名;

        @Override
        public String getString() {
            return this.name();
        }
    }

    @Override
    public DtoCreatorItemGroup execute() {

        final DtoCreatorItemGroup dtoCreatorItemGroup = DtoCreatorItemGroup.of();

        loadContent(this.getClass()).forEach(content -> {
            dtoCreatorItemGroup
                    .add(DtoCreatorItem.of(Integer.parseInt(content.get(ContentAttribute.セル項目コード.getString())),
                            content.get(ContentAttribute.セル項目名.getString())));

        });

        return dtoCreatorItemGroup;
    }

    @Override
    public List<Attribute> getAttributes() {
        final List<Attribute> attributes = new ArrayList<>(2);
        attributes.add(ContentAttribute.セル項目コード);
        attributes.add(ContentAttribute.セル項目名);

        return attributes;
    }

    @Override
    public Map<Condition, String> getConditions() {
        return null;
    }
}
