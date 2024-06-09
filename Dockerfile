# syntax = docker/dockerfile:1

# Base image
ARG RUBY_VERSION=3.3.0
FROM registry.docker.com/library/ruby:$RUBY_VERSION-slim AS base

# Set working directory
WORKDIR /rails

# Install necessary packages
RUN apt-get update -qq && \
    apt-get install --no-install-recommends -y \
    build-essential \
    git \
    libpq-dev \
    libvips \
    pkg-config \
    curl \
    postgresql-client \
    android-sdk \
    openjdk-17-jre && \
    rm -rf /var/lib/apt/lists/* /var/cache/apt/archives/*

# Copy Gemfiles and install dependencies
COPY Gemfile Gemfile.lock ./
RUN bundle config --local deployment true && \
    bundle config --local without 'development:test' && \
    bundle install --jobs 4 --retry 3 && \
    rm -rf ~/.bundle/ "${BUNDLE_PATH}"/ruby/*/cache "${BUNDLE_PATH}"/ruby/*/bundler/gems/*/.git

# Copy the application code
COPY . .

# Final stage for app image
FROM base AS final

# Copy built artifacts from the base stage
COPY --from=base /usr/local/bundle /usr/local/bundle
COPY --from=base /rails /rails

# Create a non-root user
RUN addgroup --gid 1000 rails && \
    adduser --uid 1000 --ingroup rails --home /rails --shell /bin/bash --disabled-password rails && \
    chown -R rails:rails /rails

RUN chmod +x entrypoint.sh script/wait-for-it.sh

RUN cd kotlin && ./gradlew :server:assemble 
COPY kotlin/server/build/libs/server-all.jar bin/server-all.jar

# Switch to the non-root user
USER rails
