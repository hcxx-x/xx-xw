
from pymilvus import connections, Collection


connections.connect(host="localhost", port='19530')
collection_name = "receipt"



mv_collection = Collection(name=collection_name)

# 假设你有一个 age 字段，想要删除年龄小于某个值的数据
# 首先，根据 age 字段查询出向量 id
# 假设 time_field 是时间字段的名字，target_time 是你要查询的时间点
filter_query = {"range": {"upload_time": {"lt": 1693497600}}}
search_results = mv_collection.query(expr="upload_time<1672502400", output_fields=['id'], limit=10000)

print(len(search_results))
print(search_results)

# 获取满足条件的向量 id
ids_to_delete = [result['id'] for result in search_results]

# 根据获取的 id 列表执行删除操作
# 删除会有延迟，但是确实是可以删的，延迟时间不确定，返回值delete_count是一个对象，对象中有一个属性 delete count 表示删除了多少数据， 从测试结果来看success count不代表成功删除的数量，即使为0也可以正常删除
delete_count = mv_collection.delete(expr=f'id in {ids_to_delete}')
print(f'成功删除{delete_count}条数据')

# 直接删除
#mv_collection.delete(expr="upload_time<1693497600")











