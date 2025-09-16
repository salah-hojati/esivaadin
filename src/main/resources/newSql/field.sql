INSERT INTO productdb.field (id, label, name, regular_expression, relationship_type, required, type, entity_id) VALUES (1, null, 'name', null, null, false, 'String', 1);
INSERT INTO productdb.field (id, label, name, regular_expression, relationship_type, required, type, entity_id) VALUES (2, null, 'lastname', null, null, false, 'String', 1);
INSERT INTO productdb.field (id, label, name, regular_expression, relationship_type, required, type, entity_id) VALUES (3, null, 'DateBirth', null, null, false, 'Date', 1);
INSERT INTO productdb.field (id, label, name, regular_expression, relationship_type, required, type, entity_id) VALUES (4, null, 'name', null, null, false, 'String', 2);
INSERT INTO productdb.field (id, label, name, regular_expression, relationship_type, required, type, entity_id) VALUES (5, null, 'name', null, null, false, 'String', 3);
INSERT INTO productdb.field (id, label, name, regular_expression, relationship_type, required, type, entity_id) VALUES (6, null, 'teacher', null, 'ManyToOne', false, 'Teacher', 3);
