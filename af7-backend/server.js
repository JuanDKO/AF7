require('dotenv').config();
const express = require('express');
const cors = require('cors');
const db = require('./database');

const app = express();
const PORT = process.env.PORT || 3000;

// Middleware
app.use(cors());
app.use(express.json());

// ─────────────────────────────────────────────
// Inicialización de la base de datos
// ─────────────────────────────────────────────
async function initDatabase() {
  // Crear tabla si no existe
  await db.query(`
    CREATE TABLE IF NOT EXISTS todos (
      id        SERIAL PRIMARY KEY,
      "userId"  INTEGER NOT NULL DEFAULT 1,
      title     TEXT    NOT NULL,
      completed BOOLEAN NOT NULL DEFAULT false
    )
  `);

  await db.query(`
    CREATE TABLE IF NOT EXISTS reports (
      id          SERIAL PRIMARY KEY,
      "userId"    INTEGER NOT NULL DEFAULT 1,
      description TEXT    NOT NULL,
      resolved    BOOLEAN NOT NULL DEFAULT false
    )
  `);

  // Insertar datos de ejemplo si la tabla está vacía
  const { rows } = await db.query('SELECT COUNT(*) FROM todos');
  if (parseInt(rows[0].count) === 0) {
    const tareas = [
      'Configurar el entorno de desarrollo',
      'Diseñar la arquitectura de la app',
      'Implementar la pantalla de inicio',
      'Conectar con la API REST',
      'Añadir persistencia local con Room',
      'Configurar notificaciones push',
      'Implementar escaneo Bluetooth',
      'Crear pantalla de estadísticas',
      'Añadir ajustes de usuario',
      'Realizar pruebas de integración',
    ];
    for (let i = 0; i < tareas.length; i++) {
      await db.query(
        'INSERT INTO todos ("userId", title, completed) VALUES ($1, $2, $3)',
        [1, tareas[i], i < 3] // Las 3 primeras como completadas
      );
    }
    console.log('✅ Datos de ejemplo insertados en la tabla todos');
  }
}

// ─────────────────────────────────────────────
// Rutas REST
// ─────────────────────────────────────────────

// GET /todos — Obtener todas las tareas
app.get('/todos', async (req, res) => {
  try {
    const { rows } = await db.query('SELECT * FROM todos ORDER BY id ASC');
    res.json(rows);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Error al obtener las tareas' });
  }
});

// GET /todos/:id — Obtener una tarea por ID
app.get('/todos/:id', async (req, res) => {
  try {
    const { rows } = await db.query('SELECT * FROM todos WHERE id = $1', [req.params.id]);
    if (rows.length === 0) return res.status(404).json({ error: 'Tarea no encontrada' });
    res.json(rows[0]);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Error al obtener la tarea' });
  }
});

// POST /todos — Crear una nueva tarea
app.post('/todos', async (req, res) => {
  const { userId = 1, title, completed = false } = req.body;
  if (!title) return res.status(400).json({ error: 'El campo "title" es obligatorio' });
  try {
    const { rows } = await db.query(
      'INSERT INTO todos ("userId", title, completed) VALUES ($1, $2, $3) RETURNING *',
      [userId, title, completed]
    );
    res.status(201).json(rows[0]);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Error al crear la tarea' });
  }
});

// PUT /todos/:id — Actualizar una tarea
app.put('/todos/:id', async (req, res) => {
  const { userId, title, completed } = req.body;
  try {
    const current = await db.query('SELECT * FROM todos WHERE id = $1', [req.params.id]);
    if (current.rows.length === 0) return res.status(404).json({ error: 'Tarea no encontrada' });

    const updated = {
      userId:    userId    ?? current.rows[0].userId,
      title:     title     ?? current.rows[0].title,
      completed: completed ?? current.rows[0].completed,
    };

    const { rows } = await db.query(
      'UPDATE todos SET "userId" = $1, title = $2, completed = $3 WHERE id = $4 RETURNING *',
      [updated.userId, updated.title, updated.completed, req.params.id]
    );
    res.json(rows[0]);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Error al actualizar la tarea' });
  }
});

// DELETE /todos/:id — Eliminar una tarea
app.delete('/todos/:id', async (req, res) => {
  try {
    const { rowCount } = await db.query('DELETE FROM todos WHERE id = $1', [req.params.id]);
    if (rowCount === 0) return res.status(404).json({ error: 'Tarea no encontrada' });
    res.json({ message: `Tarea ${req.params.id} eliminada correctamente` });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Error al eliminar la tarea' });
  }
});

// ─────────────────────────────────────────────
// Rutas REST — Reports (Informes)
// ─────────────────────────────────────────────

// GET /reports — Obtener todos los informes
app.get('/reports', async (req, res) => {
  try {
    const { rows } = await db.query('SELECT * FROM reports ORDER BY id DESC');
    res.json(rows);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Error al obtener los informes' });
  }
});

// POST /reports — Crear un nuevo informe
app.post('/reports', async (req, res) => {
  const { userId = 1, description, resolved = false } = req.body;
  if (!description) return res.status(400).json({ error: 'El campo "description" es obligatorio' });
  try {
    const { rows } = await db.query(
      'INSERT INTO reports ("userId", description, resolved) VALUES ($1, $2, $3) RETURNING *',
      [userId, description, resolved]
    );
    res.status(201).json(rows[0]);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Error al crear el informe' });
  }
});

// PATCH /reports/:id — Actualizar estado de un informe
app.patch('/reports/:id', async (req, res) => {
  const { resolved } = req.body;
  if (resolved === undefined) return res.status(400).json({ error: 'El campo "resolved" es obligatorio' });
  try {
    const { rows } = await db.query(
      'UPDATE reports SET resolved = $1 WHERE id = $2 RETURNING *',
      [resolved, req.params.id]
    );
    if (rows.length === 0) return res.status(404).json({ error: 'Informe no encontrado' });
    res.json(rows[0]);
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Error al actualizar el informe' });
  }
});

// ─────────────────────────────────────────────
// Arrancar servidor
// ─────────────────────────────────────────────
initDatabase()
  .then(() => {
    app.listen(PORT, () => {
      console.log(`🚀 Servidor AF7 corriendo en http://localhost:${PORT}`);
      console.log(`📋 Endpoints disponibles:`);
      console.log(`   GET    http://localhost:${PORT}/todos`);
      console.log(`   GET    http://localhost:${PORT}/todos/:id`);
      console.log(`   POST   http://localhost:${PORT}/todos`);
      console.log(`   PUT    http://localhost:${PORT}/todos/:id`);
      console.log(`   DELETE http://localhost:${PORT}/todos/:id`);
      console.log(`   GET    http://localhost:${PORT}/reports`);
      console.log(`   POST   http://localhost:${PORT}/reports`);
      console.log(`   PATCH  http://localhost:${PORT}/reports/:id`);
    });
  })
  .catch((err) => {
    console.error('❌ Error al inicializar la base de datos:', err.message);
    console.error('   Asegúrate de que Docker está corriendo y el contenedor cursoteca_db está activo.');
    process.exit(1);
  });
