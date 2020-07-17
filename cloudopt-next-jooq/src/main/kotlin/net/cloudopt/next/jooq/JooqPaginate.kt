/*
 * Copyright 2017-2020 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.cloudopt.next.jooq

import org.jooq.OrderField
import org.jooq.SelectConditionStep

/*
 * @author: Cloudopt
 * @Time: 2018/4/5
 * @Description: Pagination
 */
class JooqPaginate(query: SelectConditionStep<*>, private var count: Int, private val page: Int) {

    private var totalPage: Int = 0
    private val totalRow: Long
    private var firstPage = false
    private var lastPage = false
    private var query: SelectConditionStep<*>
    private lateinit var orderField: OrderField<*>

    init {
        this.query = query
        this.totalRow = count().toLong()
        this.totalPage = (totalRow / count.toLong()).toInt()
        if (totalRow % count.toLong() != 0L) {
            ++totalPage
        }

        if (count > totalRow) {
            this.count = totalRow.toInt()
        }

        if (totalRow != 0L && this.count <= 0 || this.page <= 0) {
            throw RuntimeException("JooqPage tips: count or page is error !")
        }

        this.firstPage = this.page == 1
        this.lastPage = this.page == this.totalPage

    }

    fun order(orderField: OrderField<*>) {
        this.orderField = orderField
    }

    fun <T> find(clazz: Class<T>): JooqPage {
        var list = query.orderBy(orderField).limit(this.count).offset(skip()).fetchInto(clazz)
        list = if (list.isNotEmpty()) {
            list.toMutableList()
        } else {
            mutableListOf()
        }
        return JooqPage(
            count,
            page,
            totalPage,
            totalRow,
            firstPage,
            lastPage,
            list
        )
    }


    private fun skip(): Int {
        return (page - 1) * count
    }

    fun count(): Int {
        return this.query.count()
    }


}