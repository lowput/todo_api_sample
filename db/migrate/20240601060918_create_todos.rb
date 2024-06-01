class CreateTodos < ActiveRecord::Migration[7.1]
  def change
    create_table :todos do |t|
      t.string :content
      t.boolean :completed
      t.datetime :deadline

      t.timestamps
    end
  end
end
