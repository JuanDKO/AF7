/**
 * seed.js — Cargador inicial de datos para la BD af7_db
 * Uso: node seed.js
 *
 * Inserta 10 tareas de ejemplo si la tabla está vacía.
 * Si ya hay datos, no hace nada (idempotente).
 */
require('dotenv').config();
const { Pool } = require('pg');

const pool = new Pool({
  host:     process.env.DB_HOST,
  port:     parseInt(process.env.DB_PORT),
  database: process.env.DB_NAME,
  user:     process.env.DB_USER,
  password: process.env.DB_PASSWORD,
});

const TAREAS = [
  { userId: 1, title: 'Configurar el entorno de desarrollo',       completed: true  },
  { userId: 1, title: 'Diseñar la arquitectura de la app',         completed: true  },
  { userId: 1, title: 'Implementar la pantalla de inicio',         completed: true  },
  { userId: 1, title: 'Conectar con la API REST',                  completed: false },
  { userId: 1, title: 'Añadir persistencia local con Room',        completed: false },
  { userId: 2, title: 'Configurar notificaciones push',            completed: false },
  { userId: 2, title: 'Implementar escaneo Bluetooth',             completed: false },
  { userId: 2, title: 'Crear pantalla de estadísticas con gráfico',completed: false },
  { userId: 2, title: 'Añadir pantalla de ajustes de usuario',     completed: false },
  { userId: 2, title: 'Realizar pruebas de integración finales',   completed: false },
];

async function seed() {
  const client = await pool.connect();
  try {
    // Crear la tabla si no existe
    await client.query(`
      CREATE TABLE IF NOT EXISTS todos (
        id        SERIAL PRIMARY KEY,
        "userId"  INTEGER NOT NULL DEFAULT 1,
        title     TEXT    NOT NULL,
        completed BOOLEAN NOT NULL DEFAULT false
      )
    `);

    // Comprobar si ya hay datos
    const { rows } = await client.query('SELECT COUNT(*) FROM todos');
    const count = parseInt(rows[0].count);

    if (count > 0) {
      console.log(`ℹ️  La tabla ya tiene ${count} tarea(s). No se insertan datos nuevos.`);
      console.log('   Si quieres reiniciar los datos, ejecuta: node seed.js --reset');
      return;
    }

    // Verificar si se pasó el flag --reset
    if (process.argv.includes('--reset')) {
      await client.query('DELETE FROM todos');
      await client.query('ALTER SEQUENCE todos_id_seq RESTART WITH 1');
      console.log('🗑️  Datos anteriores eliminados.');
    }

    // Insertar las 10 tareas
    console.log('🌱 Insertando 10 tareas de ejemplo...');
    for (const tarea of TAREAS) {
      await client.query(
        'INSERT INTO todos ("userId", title, completed) VALUES ($1, $2, $3)',
        [tarea.userId, tarea.title, tarea.completed]
      );
      console.log(`   ✅ "${tarea.title}"`);
    }

    console.log(`\n🎉 Seeder completado — ${TAREAS.length} tareas insertadas en af7_db.`);
  } finally {
    client.release();
    await pool.end();
  }
}

// Manejar --reset aunque haya datos
async function main() {
  if (process.argv.includes('--reset')) {
    const client = await pool.connect();
    try {
      await client.query('DELETE FROM todos');
      await client.query('ALTER SEQUENCE todos_id_seq RESTART WITH 1');
      console.log('🗑️  Datos anteriores eliminados.');
    } finally {
      client.release();
    }
    const client2 = await pool.connect();
    try {
      console.log('🌱 Insertando 10 tareas de ejemplo...');
      for (const tarea of TAREAS) {
        await client2.query(
          'INSERT INTO todos ("userId", title, completed) VALUES ($1, $2, $3)',
          [tarea.userId, tarea.title, tarea.completed]
        );
        console.log(`   ✅ "${tarea.title}"`);
      }
      console.log(`\n🎉 Seeder completado — ${TAREAS.length} tareas insertadas en af7_db.`);
    } finally {
      client2.release();
      await pool.end();
    }
  } else {
    await seed();
  }
}

main().catch((err) => {
  console.error('❌ Error en el seeder:', err.message);
  process.exit(1);
});
