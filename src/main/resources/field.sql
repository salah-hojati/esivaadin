INSERT INTO productdb.field (id, name, relationship_type, required, type, entity_id, label, regular_expression) VALUES (1, 'name', null, false, 'String', 1, null, null);
INSERT INTO productdb.field (id, name, relationship_type, required, type, entity_id, label, regular_expression) VALUES (2, 'lastname', null, false, 'String', 1, null, null);
INSERT INTO productdb.field (id, name, relationship_type, required, type, entity_id, label, regular_expression) VALUES (3, 'DateBirth', null, false, 'Date', 1, null, null);
INSERT INTO productdb.field (id, name, relationship_type, required, type, entity_id, label, regular_expression) VALUES (4, 'name', null, false, 'String', 2, null, null);
INSERT INTO productdb.field (id, name, relationship_type, required, type, entity_id, label, regular_expression) VALUES (5, 'name', null, false, 'String', 3, null, null);
INSERT INTO productdb.field (id, name, relationship_type, required, type, entity_id, label, regular_expression) VALUES (6, 'teacher', 'ManyToOne', false, 'Teacher', 3, null, null);
