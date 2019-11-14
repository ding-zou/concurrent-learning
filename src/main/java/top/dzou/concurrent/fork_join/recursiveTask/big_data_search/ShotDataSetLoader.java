package top.dzou.concurrent.fork_join.recursiveTask.big_data_search;

/**
 * @author dingxiang
 * @date 19-8-12 下午6:06
 */

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.csv.CsvData;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvRow;
import cn.hutool.core.text.csv.CsvUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 加载投篮数据集
 */
public class ShotDataSetLoader {
    public static List<ShotDataSet> loadCsv(String path) {
        List<ShotDataSet> res = new ArrayList<ShotDataSet>();
        CsvReader reader = CsvUtil.getReader();
//从文件中读取CSV数据
        CsvData data = reader.read(FileUtil.file(path));
        List<CsvRow> rows = data.getRows();
//遍历行
        for (CsvRow csvRow : rows) {
            //getRawList返回一个List列表，列表的每一项为CSV中的一个单元格（既逗号分隔部分）
            List<String> strList = csvRow.getRawList();
            ShotDataSet dataSet = new ShotDataSet(strList.get(0),
                    strList.get(1),strList.get(2),strList.get(3),
                    strList.get(4),strList.get(5),strList.get(6),
                    strList.get(7),strList.get(8),strList.get(9),
                    strList.get(10),strList.get(11),strList.get(12),
                    strList.get(13),strList.get(14),strList.get(15),
                    strList.get(16),strList.get(17),strList.get(18),
                    strList.get(19),strList.get(20));
            res.add(dataSet);
        }
        return res;
    }
}
