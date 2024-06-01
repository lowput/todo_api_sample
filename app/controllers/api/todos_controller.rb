class Api::TodosController < ApplicationController
  # GET /api/todos
  def index
    if params[:limit].present?
      limit = [params[:limit].to_i, 1].max
      @todos = Todo.order(created_at: :desc).limit(limit)
    else
      @todos = Todo.order(created_at: :desc)
    end
    render json: @todos
  end

  # POST /api/todos
  def create
    @todo = Todo.new(todo_params)

    if @todo.save
      render json: @todo, status: :created
    else
      render json: @todo.errors, status: :unprocessable_entity
    end
  end

  # PUT /api/todos/:id
  def update
    @todo = Todo.find(params[:id])

    if @todo.update(todo_params)
      render json: @todo
    else
      render json: @todo.errors, status: :unprocessable_entity
    end
  end

  # DELETE /api/todos/:id
  def destroy
    @todo = Todo.find(params[:id])
    @todo.destroy

    render json: @todo
  end

private

  def todo_params
    params.require(:todo).permit(:content, :completed, :deadline)
  end
end
